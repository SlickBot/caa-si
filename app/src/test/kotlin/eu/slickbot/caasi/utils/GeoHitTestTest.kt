package eu.slickbot.caasi.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.spatialk.geojson.Position

class GeoHitTestTest {

  private val square = listOf(
    Position(longitude = 0.0, latitude = 0.0),
    Position(longitude = 2.0, latitude = 0.0),
    Position(longitude = 2.0, latitude = 2.0),
    Position(longitude = 0.0, latitude = 2.0),
  )

  @Test
  fun pointInPolygon_trueForInteriorPoint() {
    assertTrue(pointInPolygon(Position(longitude = 1.0, latitude = 1.0), square))
  }

  @Test
  fun pointInPolygon_falseForExteriorPoint() {
    assertFalse(pointInPolygon(Position(longitude = 3.0, latitude = 3.0), square))
  }

  @Test
  fun distanceToPolyline_zeroWhenOnTheLine() {
    val line = listOf(Position(longitude = 0.0, latitude = 0.0), Position(longitude = 4.0, latitude = 0.0))
    assertEquals(0.0, distanceToPolyline(Position(longitude = 2.0, latitude = 0.0), line), 1e-9)
  }

  @Test
  fun distanceToPolyline_perpendicularOffset() {
    val line = listOf(Position(longitude = 0.0, latitude = 0.0), Position(longitude = 4.0, latitude = 0.0))
    assertEquals(1.0, distanceToPolyline(Position(longitude = 2.0, latitude = 1.0), line), 1e-9)
  }
}
