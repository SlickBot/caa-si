package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpatialReference(
  @Json(name = "latestWkid")
  val latestWkid: Int,
  @Json(name = "wkid")
  val wkid: Int,
)
