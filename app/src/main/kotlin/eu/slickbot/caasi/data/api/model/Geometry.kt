package eu.slickbot.caasi.data.api.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.utils.simplify
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.slickbot.caasi.utils.takeIf

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
      cachedPolygons = if (tolerance == 0.0) polygons?.firstOrNull() else polygons?.firstOrNull()?.simplify(tolerance)
      cachedPolygons
    }
  }

  fun simplifiedPolyline(tolerance: Double): List<LatLng>? {
    return if (tolerance == cachedLineTolerance) {
      cachedLine
    } else {
      cachedLineTolerance = tolerance
      cachedLine = if (tolerance == 0.0) line else line?.simplify(tolerance)
      cachedLine
    }
  }

  private fun List<List<Double>>.toLatLngList(): List<LatLng> {
    return map { (lng, lat) -> LatLng(lat, lng) }
  }

}
