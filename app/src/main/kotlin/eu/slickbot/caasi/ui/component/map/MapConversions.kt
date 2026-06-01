package eu.slickbot.caasi.ui.component.map

import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position

fun LatLng.toPosition(): Position {
  return Position(longitude = longitude, latitude = latitude)
}

fun Position.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}

fun BoundingBox.toLatLngBounds(): LatLngBounds {
  return LatLngBounds.from(northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
}

private fun closedRing(ring: List<LatLng>): List<Position> {
  val positions = ring.map { it.toPosition() }
  return if (positions.isNotEmpty() && positions.first() != positions.last()) {
    positions + listOf(positions.first())
  } else {
    positions
  }
}

fun polygonFeatureCollection(rings: List<List<LatLng>>): FeatureCollection<Polygon, Nothing?> {
  val features = rings
    .filter { it.size >= 3 }
    .map { Feature(geometry = Polygon(coordinates = listOf(closedRing(it))), properties = null) }
  return FeatureCollection(features)
}

fun lineFeatureCollection(lines: List<List<LatLng>>): FeatureCollection<LineString, Nothing?> {
  val features = lines
    .filter { it.size >= 2 }
    .map { Feature(geometry = LineString(coordinates = it.map { p -> p.toPosition() }), properties = null) }
  return FeatureCollection(features)
}

fun pointFeatureCollection(point: LatLng): FeatureCollection<Point, Nothing?> {
  return FeatureCollection(listOf(Feature(geometry = Point(coordinates = point.toPosition()), properties = null)))
}
