package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FieldInfo(
  @Json(name = "fieldName")
  val fieldName: String,
  @Json(name = "format")
  val format: Format?,
  @Json(name = "isEditable")
  val isEditable: Boolean?,
  @Json(name = "label")
  val label: String,
  @Json(name = "visible")
  val visible: Boolean,
)
