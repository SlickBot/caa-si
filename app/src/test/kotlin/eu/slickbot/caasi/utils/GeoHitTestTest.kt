package eu.slickbot.caasi.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.android.geometry.LatLng

class GeoHitTestTest {

  private val square = listOf(
    LatLng(0.0, 0.0),
    LatLng(0.0, 2.0),
    LatLng(2.0, 2.0),
    LatLng(2.0, 0.0),
  )

  @Test
  fun pointInPolygon_trueForInteriorPoint() {
    assertTrue(pointInPolygon(LatLng(1.0, 1.0), square))
  }

  @Test
  fun pointInPolygon_falseForExteriorPoint() {
    assertFalse(pointInPolygon(LatLng(3.0, 3.0), square))
  }

  @Test
  fun distanceToPolyline_zeroWhenOnTheLine() {
    val line = listOf(LatLng(0.0, 0.0), LatLng(0.0, 4.0))
    assertEquals(0.0, distanceToPolyline(LatLng(0.0, 2.0), line), 1e-9)
  }

  @Test
  fun distanceToPolyline_perpendicularOffset() {
    val line = listOf(LatLng(0.0, 0.0), LatLng(0.0, 4.0))
    assertEquals(1.0, distanceToPolyline(LatLng(1.0, 2.0), line), 1e-9)
  }
}
