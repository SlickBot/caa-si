package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Renderer(
  @Json(name = "symbol")
  val symbol: Symbol,
  @Json(name = "type")
  val type: String,
)
