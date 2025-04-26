package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Symbol(
  @Json(name = "color")
  val color: List<Int>,
  @Json(name = "outline")
  val outline: Outline,
  @Json(name = "style")
  val style: String,
  @Json(name = "type")
  val type: String,
)
