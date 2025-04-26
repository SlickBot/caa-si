package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LayersResponse(
  val authoringApp: String,
  val authoringAppVersion: String,
  val baseMap: BaseMap,
  val bookmarks: List<Bookmark>,
  val initialState: InitialState,
  val operationalLayers: List<Layer>,
  val spatialReference: SpatialReference,
  val version: String,
)
