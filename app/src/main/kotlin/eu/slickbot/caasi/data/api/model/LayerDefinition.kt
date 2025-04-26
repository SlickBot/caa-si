package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LayerDefinition(
  @Json(name = "definitionExpression")
  val definitionExpression: Any?,
  @Json(name = "drawingInfo")
  val drawingInfo: DrawingInfo?,
  @Json(name = "featureReduction")
  val featureReduction: Any?,
  @Json(name = "minScale")
  val minScale: Double?,
)
