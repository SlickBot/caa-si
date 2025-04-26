package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LayerFeaturesResponse(
  val features: List<LayerFeature>,
  val type: String,
)
