package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PopupInfo(
  @Json(name = "expressionInfos")
  val expressionInfos: List<Any>?,
  @Json(name = "fieldInfos")
  val fieldInfos: List<FieldInfo>,
  @Json(name = "popupElements")
  val popupElements: List<PopupElement>,
  @Json(name = "showAttachments")
  val showAttachments: Boolean?,
  @Json(name = "title")
  val title: String,
)
