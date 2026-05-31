package eu.slickbot.caasi.utils

import org.maplibre.android.geometry.LatLng
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Approximates a geographic circle (radius in meters) as a closed ring of
 * `segments` lat/lng vertices around `center`, using spherical offsets. ramani's
 * `Circle` is screen-pixel based, so a meters-accurate circle (e.g. a location
 * accuracy halo) must be drawn as a `Polygon` of these vertices instead.
 */
fun circlePolygon(center: LatLng, radiusMeters: Double, segments: Int = 64): List<LatLng> {
  val earthRadius = 6378137.0
  val angularDistance = radiusMeters / earthRadius
  val lat = Math.toRadians(center.latitude)
  val lng = Math.toRadians(center.longitude)
  val result = ArrayList<LatLng>(segments)
  for (i in 0 until segments) {
    val bearing = 2.0 * Math.PI * i / segments
    val lat2 = asin(sin(lat) * cos(angularDistance) + cos(lat) * sin(angularDistance) * cos(bearing))
    val lng2 = lng + atan2(
      sin(bearing) * sin(angularDistance) * cos(lat),
      cos(angularDistance) - sin(lat) * sin(lat2),
    )
    result.add(LatLng(Math.toDegrees(lat2), Math.toDegrees(lng2)))
  }
  return result
}

/**
 * Douglas-Peucker simplification of a lat/lng path. `tolerance` is in degrees
 * (planar), matching how the previous maps-utils call was driven from zoom.
 */
fun simplify(points: List<LatLng>, tolerance: Double): List<LatLng> {
  if (points.size <= 2 || tolerance <= 0.0) {
    return points
  }
  val keep = BooleanArray(points.size)
  keep[0] = true
  keep[points.size - 1] = true
  simplifySection(points, 0, points.size - 1, tolerance, keep)
  return points.filterIndexed { index, _ -> keep[index] }
}

private fun simplifySection(
  points: List<LatLng>,
  start: Int,
  end: Int,
  tolerance: Double,
  keep: BooleanArray,
) {
  if (end <= start + 1) {
    return
  }
  var maxDist = -1.0
  var maxIndex = start
  val a = points[start]
  val b = points[end]
  for (i in (start + 1) until end) {
    val dist = perpendicularDistance(points[i], a, b)
    if (dist > maxDist) {
      maxDist = dist
      maxIndex = i
    }
  }
  if (maxDist > tolerance) {
    keep[maxIndex] = true
    simplifySection(points, start, maxIndex, tolerance, keep)
    simplifySection(points, maxIndex, end, tolerance, keep)
  }
}

private fun perpendicularDistance(p: LatLng, a: LatLng, b: LatLng): Double {
  val ax = a.longitude
  val ay = a.latitude
  val bx = b.longitude
  val by = b.latitude
  val px = p.longitude
  val py = p.latitude
  val dx = bx - ax
  val dy = by - ay
  val lenSq = dx * dx + dy * dy
  if (lenSq == 0.0) {
    val ddx = px - ax
    val ddy = py - ay
    return sqrt(ddx * ddx + ddy * ddy)
  }
  var t = ((px - ax) * dx + (py - ay) * dy) / lenSq
  t = t.coerceIn(0.0, 1.0)
  val projX = ax + t * dx
  val projY = ay + t * dy
  val ddx = px - projX
  val ddy = py - projY
  return sqrt(ddx * ddx + ddy * ddy)
}
