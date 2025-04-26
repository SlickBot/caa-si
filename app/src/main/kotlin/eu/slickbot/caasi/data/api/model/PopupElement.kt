package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PopupElement(
  @Json(name = "description")
  val description: String?,
  @Json(name = "displayType")
  val displayType: String?,
  @Json(name = "fieldInfos")
  val fieldInfos: List<FieldInfo>?,
  @Json(name = "title")
  val title: String?,
  @Json(name = "type")
  val type: String,
)
