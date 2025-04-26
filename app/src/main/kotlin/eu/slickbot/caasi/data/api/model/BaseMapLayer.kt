package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseMapLayer(
  @Json(name = "blendMode")
  val blendMode: String,
  @Json(name = "id")
  val id: String,
  @Json(name = "layerType")
  val layerType: String,
  @Json(name = "opacity")
  val opacity: Int?,
  @Json(name = "title")
  val title: String,
  @Json(name = "url")
  val url: String,
)
