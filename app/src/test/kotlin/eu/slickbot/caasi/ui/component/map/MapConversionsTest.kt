package eu.slickbot.caasi.ui.component.map

import org.junit.Assert.assertEquals
import org.junit.Test
import org.maplibre.spatialk.geojson.Position

class MapConversionsTest {

  @Test
  fun polygonFeatureCollection_closesRingAndCountsFeatures() {
    val ring = listOf(
      Position(longitude = 14.0, latitude = 45.0),
      Position(longitude = 16.0, latitude = 45.0),
      Position(longitude = 16.0, latitude = 46.0),
    )
    val fc = polygonFeatureCollection(listOf(ring, ring))
    assertEquals(2, fc.features.size)
    val firstRing = fc.features.first().geometry.coordinates.first()
    assertEquals(4, firstRing.size)
    assertEquals(firstRing.first(), firstRing.last())
  }

  @Test
  fun lineFeatureCollection_buildsLineStrings() {
    val line = listOf(
      Position(longitude = 14.0, latitude = 45.0),
      Position(longitude = 15.0, latitude = 46.0),
    )
    val fc = lineFeatureCollection(listOf(line))
    assertEquals(1, fc.features.size)
    val coords = fc.features.first().geometry.coordinates
    assertEquals(2, coords.size)
  }

  @Test
  fun pointFeatureCollection_buildsSinglePoint() {
    val fc = pointFeatureCollection(Position(longitude = 15.0, latitude = 45.9))
    assertEquals(1, fc.features.size)
    val p = fc.features.first().geometry.coordinates
    assertEquals(45.9, p.latitude, 1e-9)
  }
}
