package eu.slickbot.caasi.data.repo

import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType
import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.LayerFeature
import eu.slickbot.caasi.data.prefs.SettingsPrefs
import eu.slickbot.caasi.utils.asyncFlatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CaaSiRepository(
  private val api: CaaSiApi,
  private val settingsPrefs: SettingsPrefs,
) {

  suspend fun getLayers(): List<Layer> {
    return withContext(Dispatchers.IO) {
      api.getLayers()
    }
  }

  suspend fun getFeatures(layer: Layer, bounds: LatLngBounds? = null): List<LayerFeature> {
    return withContext(Dispatchers.IO) {
      api.getLayerFeatures(layer, bounds)
    }
  }

  suspend fun getBuiltFeatures(layers: List<Layer>, bounds: LatLngBounds?, zoom: Float): List<LayerFeature> {
    return withContext(Dispatchers.Default) {
      if (zoom < LAYER_BUILT_ZOOM_THRESHOLD || bounds == null) {
        emptyList()
      } else {
        layers
          .filter { it.title == LAYER_BUILT_TITLE }
          .asyncFlatMap { getFeatures(it, bounds) }
      }
    }
  }

  suspend fun getOtherFeatures(layers: List<Layer>): List<LayerFeature> {
    return withContext(Dispatchers.Default) {
      layers
        .filter { it.title != LAYER_BUILT_TITLE }
        .asyncFlatMap { getFeatures(it) }
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

  fun getSelectedMapType(): Flow<MapType> {
    return settingsPrefs.mapTypesFlow.map { MapType.valueOf(it) }
  }

  suspend fun saveSelectedMapType(mapType: MapType) {
    return settingsPrefs.saveMapType(mapType.name)
  }

  companion object {
    private const val LAYER_BUILT_ZOOM_THRESHOLD = 13f
    private const val LAYER_BUILT_TITLE = "Pozidano-built"
    private const val LAYER_BORDER_TITLE = "FIR"
  }

}
