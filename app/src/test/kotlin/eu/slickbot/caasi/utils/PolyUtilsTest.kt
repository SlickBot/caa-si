package eu.slickbot.caasi.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.android.geometry.LatLng

class PolyUtilsTest {

  @Test
  fun simplify_removesNearlyCollinearMiddlePoints() {
    val line = listOf(
      LatLng(0.0, 0.0),
      LatLng(0.0, 1.0),
      LatLng(0.0, 2.0),
    )
    val result = simplify(line, tolerance = 1.0)
    assertEquals(2, result.size)
    assertEquals(LatLng(0.0, 0.0), result.first())
    assertEquals(LatLng(0.0, 2.0), result.last())
  }

  @Test
  fun simplify_keepsPointsThatExceedTolerance() {
    val line = listOf(
      LatLng(0.0, 0.0),
      LatLng(5.0, 1.0),
      LatLng(0.0, 2.0),
    )
    val result = simplify(line, tolerance = 1.0)
    assertEquals(3, result.size)
  }

  @Test
  fun simplify_returnsInputWhenTwoOrFewerPoints() {
    val line = listOf(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
    assertTrue(simplify(line, tolerance = 0.5) === line)
  }
}
