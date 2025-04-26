package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Bookmark(
  @Json(name = "extent")
  val extent: Extent,
  @Json(name = "name")
  val name: String,
  @Json(name = "thumbnail")
  val thumbnail: Thumbnail,
  @Json(name = "viewpoint")
  val viewpoint: Viewpoint,
)
