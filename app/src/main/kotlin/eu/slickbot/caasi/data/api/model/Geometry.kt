package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

//    val polygons: List<List<LatLng>>? by lazy {
//        if (type == Type.POLYGON) (coordinatesRaw as List<List<List<Double>>>).map { polygon -> polygon.map { LatLng(it[1], it[0]) } } else null
//    }
//    val line: List<LatLng>? by lazy {
//        if (type == Type.LINE) (coordinatesRaw as List<List<Double>>).map { LatLng(it[1], it[0]) } else null
//    }
//
//    @Transient
//    private var cachedPolygonsTolerance: Double = Double.MIN_VALUE
//    @Transient
//    private var cachedPolygons: List<LatLng>? = emptyList()
//    @Transient
//    private var cachedLineTolerance: Double = Double.MIN_VALUE
//    @Transient
//    private var cachedLine: List<LatLng>? = emptyList()
//
//    fun simplifiedPolygons(tolerance: Double): List<LatLng>? {
//        return if (tolerance == cachedPolygonsTolerance) {
//            cachedPolygons
//        } else {
//            cachedPolygonsTolerance = tolerance
//            cachedPolygons = if (tolerance == 0.0) polygons?.firstOrNull() else polygons?.firstOrNull()?.simplify(tolerance)
//            cachedPolygons
//        }
//    }
//
//    fun simplifiedPolyline(tolerance: Double): List<LatLng>? {
//        return if (tolerance == cachedLineTolerance) {
//            cachedLine
//        } else {
//            cachedLineTolerance = tolerance
//            cachedLine = if (tolerance == 0.0) line else line?.simplify(tolerance)
//            cachedLine
//        }
//    }

//    val coordinates: List<LatLng>
//        get() = when (type) {
//            Type.POLYGON -> polygons.flatMap { it.coords }
//            Type.LINE -> line.coords
//        }

//    data class Coord(
//        val lat: Double,
//        val lng: Double,
//    )

//    data class Polygon(
//        val coords: List<LatLng>,
//    )

//    data class Line(
//        val coords: List<LatLng>,
//    )
}
