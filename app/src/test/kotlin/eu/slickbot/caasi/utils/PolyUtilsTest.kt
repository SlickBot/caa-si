package eu.slickbot.caasi.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.spatialk.geojson.Position
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PolyUtilsTest {

  private val earthRadius = 6378137.0

  // Great-circle distance in meters on the same sphere circlePolygon uses, so the
  // radius assertions can be tight (it is the inverse of circlePolygon's offset math).
  private fun haversineMeters(a: Position, b: Position): Double {
    val lat1 = Math.toRadians(a.latitude)
    val lat2 = Math.toRadians(b.latitude)
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLng = Math.toRadians(b.longitude - a.longitude)
    val s1 = sin(dLat / 2)
    val s2 = sin(dLng / 2)
    val h = s1 * s1 + cos(lat1) * cos(lat2) * s2 * s2
    return 2.0 * earthRadius * asin(sqrt(h))
  }

  @Test
  fun circlePolygon_returnsRequestedNumberOfVertices() {
    val ring = circlePolygon(Position(longitude = 15.0, latitude = 45.0), radiusMeters = 500.0, segments = 12)
    assertEquals(12, ring.size)
  }

  @Test
  fun circlePolygon_defaultsTo64Segments() {
    val ring = circlePolygon(Position(longitude = 15.0, latitude = 45.0), radiusMeters = 500.0)
    assertEquals(64, ring.size)
  }

  @Test
  fun circlePolygon_everyVertexIsAtTheGivenRadius() {
    val center = Position(longitude = 15.0, latitude = 45.0)
    val radius = 1000.0
    val ring = circlePolygon(center, radiusMeters = radius, segments = 32)
    ring.forEach { vertex ->
      assertEquals(radius, haversineMeters(center, vertex), 0.1)
    }
  }

  @Test
  fun circlePolygon_firstVertexIsDueNorthOfCenter() {
    val center = Position(longitude = 15.0, latitude = 45.0)
    val ring = circlePolygon(center, radiusMeters = 1000.0, segments = 8)
    val north = ring.first()
    // Vertex 0 is at bearing 0, so it sits directly north: same longitude, higher latitude.
    assertEquals(center.longitude, north.longitude, 1e-9)
    assertTrue(north.latitude > center.latitude)
  }

  @Test
  fun circlePolygon_doesNotRepeatTheFirstVertex() {
    // Closing the ring is the caller's job (closedRing); circlePolygon leaves it open.
    val ring = circlePolygon(Position(longitude = 15.0, latitude = 45.0), radiusMeters = 500.0, segments = 16)
    assertNotEquals(ring.first(), ring.last())
  }
}
