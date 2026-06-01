package eu.slickbot.caasi.ui.component.map

import org.junit.Assert.assertEquals
import org.junit.Test
import org.maplibre.android.geometry.LatLng
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

class MapConversionsTest {

  @Test
  fun latLng_toPosition_preservesLatLng() {
    val pos = LatLng(45.9, 15.0).toPosition()
    assertEquals(15.0, pos.longitude, 1e-9)
    assertEquals(45.9, pos.latitude, 1e-9)
  }

  @Test
  fun position_toLatLng_roundTrips() {
    val original = LatLng(45.9, 15.0)
    val round = original.toPosition().toLatLng()
    assertEquals(original.latitude, round.latitude, 1e-9)
    assertEquals(original.longitude, round.longitude, 1e-9)
  }

  @Test
  fun boundingBox_toLatLngBounds_mapsCorners() {
    val bbox = BoundingBox(west = 14.0, south = 45.0, east = 16.0, north = 46.0)
    val bounds = bbox.toLatLngBounds()
    assertEquals(46.0, bounds.latitudeNorth, 1e-9)
    assertEquals(45.0, bounds.latitudeSouth, 1e-9)
    assertEquals(16.0, bounds.longitudeEast, 1e-9)
    assertEquals(14.0, bounds.longitudeWest, 1e-9)
  }

  @Test
  fun polygonFeatureCollection_closesRingAndCountsFeatures() {
    val ring = listOf(LatLng(45.0, 14.0), LatLng(45.0, 16.0), LatLng(46.0, 16.0))
    val fc = polygonFeatureCollection(listOf(ring, ring))
    assertEquals(2, fc.features.size)
    val firstRing = (fc.features.first().geometry as org.maplibre.spatialk.geojson.Polygon).coordinates.first()
    assertEquals(4, firstRing.size)
    assertEquals(firstRing.first(), firstRing.last())
  }

  @Test
  fun lineFeatureCollection_buildsLineStrings() {
    val line = listOf(LatLng(45.0, 14.0), LatLng(46.0, 15.0))
    val fc = lineFeatureCollection(listOf(line))
    assertEquals(1, fc.features.size)
    val coords = (fc.features.first().geometry as org.maplibre.spatialk.geojson.LineString).coordinates
    assertEquals(2, coords.size)
  }

  @Test
  fun pointFeatureCollection_buildsSinglePoint() {
    val fc = pointFeatureCollection(LatLng(45.9, 15.0))
    assertEquals(1, fc.features.size)
    val p = (fc.features.first().geometry as org.maplibre.spatialk.geojson.Point).coordinates
    assertEquals(45.9, p.latitude, 1e-9)
  }
}
