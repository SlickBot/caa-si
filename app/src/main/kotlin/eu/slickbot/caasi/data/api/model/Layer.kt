package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Layer(
  val id: String,
  val itemId: String,
  val title: String,
  val url: String,
  val popupInfo: PopupInfo,
  val layerDefinition: LayerDefinition?,

//    @Json(name = "blendMode")
//    val blendMode: String?,
//    @Json(name = "disablePopup")
//    val disablePopup: Boolean?,
//    @Json(name = "featureEffect")
//    val featureEffect: Any?,
//    @Json(name = "opacity")
//    val opacity: Double?,
//    @Json(name = "showLabels")
//    val showLabels: Boolean?,
//    @Json(name = "showLegend")
//    val showLegend: Boolean?,

  val layerType: String,
)
