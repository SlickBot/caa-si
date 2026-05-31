package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.slickbot.caasi.utils.simplify
import eu.slickbot.caasi.utils.takeIf
import org.maplibre.android.geometry.LatLng

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

  val polygons: List<List<LatLng>>? = takeIf(type == Type.POLYGON) {
    @Suppress("UNCHECKED_CAST")
    (coordinatesRaw as List<List<List<Double>>>).map { it.toLatLngList() }
  }

  val line: List<LatLng>? = takeIf(type == Type.LINE) {
    @Suppress("UNCHECKED_CAST")
    (coordinatesRaw as List<List<Double>>).toLatLngList()
  }

  @Transient
  private var cachedPolygonsTolerance: Double = Double.MIN_VALUE

  @Transient
  private var cachedPolygons: List<LatLng>? = emptyList()

  @Transient
  private var cachedLineTolerance: Double = Double.MIN_VALUE

  @Transient
  private var cachedLine: List<LatLng>? = emptyList()

  fun simplifiedPolygons(tolerance: Double): List<LatLng>? {
    return if (tolerance == cachedPolygonsTolerance) {
      cachedPolygons
    } else {
      cachedPolygonsTolerance = tolerance
      val first = polygons?.firstOrNull()
      cachedPolygons = when {
        first == null -> null
        tolerance == 0.0 -> first
        else -> simplify(first, tolerance)
      }
      cachedPolygons
    }
  }

  fun simplifiedPolyline(tolerance: Double): List<LatLng>? {
    return if (tolerance == cachedLineTolerance) {
      cachedLine
    } else {
      cachedLineTolerance = tolerance
      val l = line
      cachedLine = when {
        l == null -> null
        tolerance == 0.0 -> l
        else -> simplify(l, tolerance)
      }
      cachedLine
    }
  }

  private fun List<List<Double>>.toLatLngList(): List<LatLng> {
    return map { (lng, lat) -> LatLng(lat, lng) }
  }

}
