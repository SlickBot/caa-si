package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InitialState(
  @Json(name = "viewpoint")
  val viewpoint: Viewpoint,
)
