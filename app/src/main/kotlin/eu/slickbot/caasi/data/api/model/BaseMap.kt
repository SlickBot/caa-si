package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseMap(
  @Json(name = "baseMapLayers")
  val baseMapLayers: List<BaseMapLayer>,
  @Json(name = "title")
  val title: String,
)
