package eu.slickbot.caasi.utils

import org.maplibre.spatialk.geojson.Position
import kotlin.math.sqrt

/** Ray-casting point-in-polygon on planar lng/lat. Ring need not be closed. */
fun pointInPolygon(point: Position, ring: List<Position>): Boolean {
  if (ring.size < 3) {
    return false
  }
  val x = point.longitude
  val y = point.latitude
  var inside = false
  var j = ring.size - 1
  for (i in ring.indices) {
    val xi = ring[i].longitude
    val yi = ring[i].latitude
    val xj = ring[j].longitude
    val yj = ring[j].latitude
    val intersects = (yi > y) != (yj > y) &&
      x < (xj - xi) * (y - yi) / (yj - yi) + xi
    if (intersects) {
      inside = !inside
    }
    j = i
  }
  return inside
}

/** Minimum planar distance (degrees) from a point to a polyline. */
fun distanceToPolyline(point: Position, line: List<Position>): Double {
  if (line.isEmpty()) {
    return Double.MAX_VALUE
  }
  if (line.size == 1) {
    return planarDistance(point, line[0])
  }
  var min = Double.MAX_VALUE
  for (i in 0 until line.size - 1) {
    val d = distanceToSegment(point, line[i], line[i + 1])
    if (d < min) {
      min = d
    }
  }
  return min
}

private fun distanceToSegment(p: Position, a: Position, b: Position): Double {
  val ax = a.longitude
  val ay = a.latitude
  val dx = b.longitude - ax
  val dy = b.latitude - ay
  val lenSq = dx * dx + dy * dy
  if (lenSq == 0.0) {
    return planarDistance(p, a)
  }
  var t = ((p.longitude - ax) * dx + (p.latitude - ay) * dy) / lenSq
  t = t.coerceIn(0.0, 1.0)
  val projX = ax + t * dx
  val projY = ay + t * dy
  val ddx = p.longitude - projX
  val ddy = p.latitude - projY
  return sqrt(ddx * ddx + ddy * ddy)
}

private fun planarDistance(a: Position, b: Position): Double {
  val dx = a.longitude - b.longitude
  val dy = a.latitude - b.latitude
  return sqrt(dx * dx + dy * dy)
}
