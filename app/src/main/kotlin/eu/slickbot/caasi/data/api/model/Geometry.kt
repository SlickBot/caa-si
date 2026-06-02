package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.slickbot.caasi.utils.takeIf
import org.maplibre.spatialk.geojson.Position

@JsonClass(generateAdapter = true)
data class Geometry(
  @Json(name = "type")
  val typeRaw: String,
  @Json(name = "coordinates")
  val coordinatesRaw: List<List<Any>>,
) {

  val type = Type.entries.first { it.text == typeRaw }

  enum class Type(val text: String) {
    POLYGON("Polygon"),
    LINE("LineString"),
  }

  val polygons: List<List<Position>>? = takeIf(type == Type.POLYGON) {
    @Suppress("UNCHECKED_CAST")
    (coordinatesRaw as List<List<List<Double>>>).map { it.toPositionList() }
  }

  val line: List<Position>? = takeIf(type == Type.LINE) {
    @Suppress("UNCHECKED_CAST")
    (coordinatesRaw as List<List<Double>>).toPositionList()
  }

  private fun List<List<Double>>.toPositionList(): List<Position> {
    return map { (lng, lat) -> Position(longitude = lng, latitude = lat) }
  }

}
