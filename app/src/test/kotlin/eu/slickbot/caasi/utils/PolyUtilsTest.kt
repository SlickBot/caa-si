package eu.slickbot.caasi.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.spatialk.geojson.Position

class PolyUtilsTest {

  @Test
  fun simplify_removesNearlyCollinearMiddlePoints() {
    val line = listOf(
      Position(longitude = 0.0, latitude = 0.0),
      Position(longitude = 1.0, latitude = 0.0),
      Position(longitude = 2.0, latitude = 0.0),
    )
    val result = simplify(line, tolerance = 1.0)
    assertEquals(2, result.size)
    assertEquals(Position(longitude = 0.0, latitude = 0.0), result.first())
    assertEquals(Position(longitude = 2.0, latitude = 0.0), result.last())
  }

  @Test
  fun simplify_keepsPointsThatExceedTolerance() {
    val line = listOf(
      Position(longitude = 0.0, latitude = 0.0),
      Position(longitude = 1.0, latitude = 5.0),
      Position(longitude = 2.0, latitude = 0.0),
    )
    val result = simplify(line, tolerance = 1.0)
    assertEquals(3, result.size)
  }

  @Test
  fun simplify_returnsInputWhenTwoOrFewerPoints() {
    val line = listOf(Position(longitude = 1.0, latitude = 1.0), Position(longitude = 2.0, latitude = 2.0))
    assertTrue(simplify(line, tolerance = 0.5) === line)
  }
}
