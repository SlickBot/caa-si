package eu.slickbot.caasi.ui.component.map

import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position

private fun closedRing(ring: List<Position>): List<Position> {
  return if (ring.isNotEmpty() && ring.first() != ring.last()) {
    ring + listOf(ring.first())
  } else {
    ring
  }
}

fun polygonFeatureCollection(rings: List<List<Position>>): FeatureCollection<Polygon, Nothing?> {
  val features = rings
    .filter { it.size >= 3 }
    .map { Feature(geometry = Polygon(coordinates = listOf(closedRing(it))), properties = null) }
  return FeatureCollection(features)
}

fun lineFeatureCollection(lines: List<List<Position>>): FeatureCollection<LineString, Nothing?> {
  val features = lines
    .filter { it.size >= 2 }
    .map { Feature(geometry = LineString(coordinates = it), properties = null) }
  return FeatureCollection(features)
}

fun pointFeatureCollection(point: Position): FeatureCollection<Point, Nothing?> {
  return FeatureCollection(listOf(Feature(geometry = Point(coordinates = point), properties = null)))
}
