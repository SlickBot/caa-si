package eu.slickbot.caasi.utils

import org.maplibre.spatialk.geojson.Position
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Approximates a geographic circle (radius in meters) as a closed ring of
 * `segments` lat/lng vertices around `center`, using spherical offsets. A map
 * `Circle` is screen-pixel based, so a meters-accurate circle (e.g. a location
 * accuracy halo) must be drawn as a `Polygon` of these vertices instead.
 */
fun circlePolygon(center: Position, radiusMeters: Double, segments: Int = 64): List<Position> {
  val earthRadius = 6378137.0
  val angularDistance = radiusMeters / earthRadius
  val lat = Math.toRadians(center.latitude)
  val lng = Math.toRadians(center.longitude)
  val result = ArrayList<Position>(segments)
  for (i in 0 until segments) {
    val bearing = 2.0 * Math.PI * i / segments
    val lat2 = asin(sin(lat) * cos(angularDistance) + cos(lat) * sin(angularDistance) * cos(bearing))
    val lng2 = lng + atan2(
      sin(bearing) * sin(angularDistance) * cos(lat),
      cos(angularDistance) - sin(lat) * sin(lat2),
    )
    result.add(Position(longitude = Math.toDegrees(lng2), latitude = Math.toDegrees(lat2)))
  }
  return result
}
