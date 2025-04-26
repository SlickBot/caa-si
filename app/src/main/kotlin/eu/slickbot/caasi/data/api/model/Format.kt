package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Format(
  @Json(name = "digitSeparator")
  val digitSeparator: Boolean,
  @Json(name = "places")
  val places: Int,
)
