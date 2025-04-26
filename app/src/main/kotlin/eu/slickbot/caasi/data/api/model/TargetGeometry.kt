package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TargetGeometry(
  @Json(name = "spatialReference")
  val spatialReference: SpatialReference,
  @Json(name = "xmax")
  val xmax: Double,
  @Json(name = "xmin")
  val xmin: Double,
  @Json(name = "ymax")
  val ymax: Double,
  @Json(name = "ymin")
  val ymin: Double,
)
