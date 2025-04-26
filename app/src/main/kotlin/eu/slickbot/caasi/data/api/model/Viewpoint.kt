package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Viewpoint(
  @Json(name = "rotation")
  val rotation: Int?,
  @Json(name = "scale")
  val scale: Double?,
  @Json(name = "targetGeometry")
  val targetGeometry: TargetGeometry,
)
