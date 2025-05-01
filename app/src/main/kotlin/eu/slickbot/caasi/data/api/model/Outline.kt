package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Outline(
  @Json(name = "color")
  val color: List<Int>,
  @Json(name = "style")
  val style: String,
  @Json(name = "type")
  val type: String,
  @Json(name = "width")
  val width: Double,
)
