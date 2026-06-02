package eu.slickbot.caasi.data.repo

import com.squareup.moshi.Moshi
import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.LayerFeature
import eu.slickbot.caasi.data.api.model.MapFeature
import eu.slickbot.caasi.data.db.dao.CacheDao
import eu.slickbot.caasi.data.db.entity.LayerEntity
import eu.slickbot.caasi.data.db.entity.LayerFeatureEntity
import eu.slickbot.caasi.data.prefs.MapTheme
import eu.slickbot.caasi.data.prefs.SettingsPrefs
import eu.slickbot.caasi.utils.asyncFlatMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.maplibre.spatialk.geojson.BoundingBox

class CaaSiRepository(
  private val api: CaaSiApi,
  private val cache: CacheDao,
  moshi: Moshi,
  private val settingsPrefs: SettingsPrefs,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

  companion object {
    const val LAYER_BUILT_ZOOM_THRESHOLD = 13f
    private const val LAYER_BUILT_TITLE = "Pozidano-built"
    private const val LAYER_BORDER_TITLE = "FIR"
  }

  private val layerAdapter = moshi.adapter(Layer::class.java)
  private val featureAdapter = moshi.adapter(LayerFeature::class.java)

  fun layersFlow(): Flow<List<Layer>> = cache.flowLayers().map { rows ->
    rows.mapNotNull { row -> runCatching { layerAdapter.fromJson(row.json) }.getOrNull() }
  }

  fun mapFeaturesFlow(): Flow<List<MapFeature>> = combine(
    cache.flowLayers(),
    cache.flowFeatures(),
  ) { layerRows, featureRows ->
    val layersById = layerRows.mapNotNull { row ->
      runCatching { layerAdapter.fromJson(row.json) }.getOrNull()?.let { row.id to it }
    }.toMap()
    featureRows.mapNotNull { row ->
      val layer = layersById[row.layerId] ?: return@mapNotNull null
      val feature = runCatching { featureAdapter.fromJson(row.json) }.getOrNull()
        ?: return@mapNotNull null
      MapFeature(layer, feature)
    }
  }

  suspend fun refreshAll() {
    withContext(Dispatchers.IO) {
      val layers = api.getLayers()
      val nonBuilt = layers.filter { it.title != LAYER_BUILT_TITLE }

      // any failed feature fetch throws; in that case we don't write to DB
      val features = nonBuilt.asyncFlatMap { layer ->
        api.getLayerFeatures(layer).map { layer.id to it }
      }

      // only commit when we got real data: at least one layer AND at least one feature parsed
      if (layers.isEmpty() || features.isEmpty()) return@withContext

      val layerEntities = layers.mapIndexed { index, layer ->
        LayerEntity(id = layer.id, position = index, json = layerAdapter.toJson(layer))
      }
      val featureEntities = features.map { (layerId, feature) ->
        LayerFeatureEntity(
          layerId = layerId,
          featureId = feature.id,
          json = featureAdapter.toJson(feature),
        )
      }

      cache.replaceAll(layerEntities, featureEntities)
    }
  }

  suspend fun getBuiltFeatures(
    layers: List<Layer>,
    bounds: BoundingBox?,
    zoom: Float,
  ): List<MapFeature> = withContext(ioDispatcher) {
    if (zoom < LAYER_BUILT_ZOOM_THRESHOLD || bounds == null) {
      emptyList()
    } else {
      layers
        .filter { it.title == LAYER_BUILT_TITLE }
        .asyncFlatMap { layer -> api.getLayerFeatures(layer, bounds).map { MapFeature(layer, it) } }
    }
  }

  fun getSelectedLayers(allLayers: List<Layer>): Flow<List<Layer>> {
    return settingsPrefs.layersFlow.map { selected ->
      if (selected == null) {
        allLayers.filter { it.title != LAYER_BORDER_TITLE }
      } else {
        allLayers.filter { it.id in selected }
      }
    }
  }

  suspend fun saveSelectedLayers(layers: List<Layer>) {
    val ids = withContext(Dispatchers.Default) { layers.map { it.id }.toSet() }
    return settingsPrefs.saveLayers(ids)
  }

  fun getSelectedMapTheme(): Flow<MapTheme> {
    return settingsPrefs.mapThemeFlow.map { name ->
      runCatching { MapTheme.valueOf(name) }.getOrDefault(MapTheme.SYSTEM)
    }
  }

  suspend fun saveSelectedMapTheme(theme: MapTheme) {
    return settingsPrefs.saveMapTheme(theme.name)
  }
}
