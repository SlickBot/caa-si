package eu.slickbot.caasi.data.api

import eu.slickbot.caasi.createOkHttpClient
import org.junit.Test

class CaaSiApiTest {

  companion object {
    private val client = createOkHttpClient()
    private val api = CaaSiApi(client)
  }

  @Test
  fun is_base_id_same() {
    val baseId = api.getBaseId()
    println("baseId = $baseId")
    assert(baseId == CaaSiApi.Companion.DEFAULT_BASE_ID)
  }

  @Test
  fun is_item_id_same() {
    val itemId = api.getItemId()
    println("itemId = $itemId")
    assert(itemId == CaaSiApi.Companion.DEFAULT_ITEM_ID)
  }

  @Test
  fun can_get_baseUrl() {
    val baseUrl = api.getBaseUrl()
    println("baseUrl = $baseUrl")
  }

  @Test
  fun can_get_baseId() {
    val id = api.getBaseId()
    println("id = $id")
  }

  @Test
  fun can_get_itemId() {
    val itemId = api.getItemId()
    println("itemId = $itemId")
  }

  @Test
  fun can_get_data() {
    val data = api.getData()
    println("data = $data")
  }

  @Test
  fun can_get_layersResponse() {
    val layersResponse = api.getLayersResponse()
    println("layersResponse = $layersResponse")
  }

  @Test
  fun can_get_layers() {
    val layers = api.getLayers()
    println("layers = $layers")

    for (layer in layers) {
      println(layer.title)
      for (info in layer.popupInfo.fieldInfos) {
        println("\t${info.fieldName}")
        println("\t\t\"${info.label}\"")
      }
    }
  }

//  @Test
//  fun can_get_featuresResponse() {
//    val layers = api.getLayers(CaaSiApi.Companion.DEFAULT_ITEM_ID)
//    val layer = layers.random()
//    val featuresResponse = api.getLayerFeaturesResponse(layer)
//    println("featuresResponse = $featuresResponse")
//  }
//
//  @Test
//  fun can_get_features() {
//    val layers = api.getLayers(CaaSiApi.Companion.DEFAULT_ITEM_ID)
//
//    for (layer in layers) {
//      for (feature in api.getLayerFeatures(layer)) {
//        println("${layer.id} - ${layer.title}")
//        if (feature.geometry.type == Geometry.Type.POLYGON) {
//          println(feature.geometry.polygons)
//        }
//        if (feature.geometry.type == Geometry.Type.LINE) {
//          println(feature.geometry.line)
//        }
//      }
//    }
//  }

//  @Test
//  fun compare() {
//    runBlocking {
//      val timeOld = measureTime { getLayersWithFeaturesOld() }
//      println("timeOld = $timeOld")
//
//      val timeNew = measureTime { getLayersWithFeatures() }
//      println("timeNew = $timeNew")
//    }
//  }

//  suspend fun getLayersWithFeaturesOld(): List<LayerWithFeatures> {
//    return withContext(Dispatchers.IO) {
//      api.getLayers().map { layer ->
//        LayerWithFeatures(layer, api.getLayerFeatures(layer))
//      }
//    }
//  }
//
//  suspend fun getLayersWithFeatures(): List<LayerWithFeatures> {
//    return withContext(Dispatchers.IO) {
//      val layers = api.getLayers()
//      val layersOut = Array<LayerWithFeatures?>(layers.size) { null }
//
//      layers.mapIndexed { i, layer ->
//        launch {
//          layersOut[i] = LayerWithFeatures(layer, api.getLayerFeatures(layer))
//        }
//      }.joinAll()
//
//      layersOut.filterNotNull()
//    }
//  }

}
