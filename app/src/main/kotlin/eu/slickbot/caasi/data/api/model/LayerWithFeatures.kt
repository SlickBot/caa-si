package eu.slickbot.caasi.data.api.model

data class LayerWithFeatures(
  val id: String,
  val itemId: String,
  val title: String,
  val url: String,
  val layerDefinition: LayerDefinition?,
//  val popupInfo: PopupInfo,
  val features: List<LayerFeature>,
) {

  constructor(layer: Layer, features: List<LayerFeature>) : this(
    id = layer.id,
    itemId = layer.itemId,
    title = layer.title,
    url = layer.url,
    layerDefinition = layer.layerDefinition,
//    popupInfo = layer.popupInfo,
    features = features,
  )
}
