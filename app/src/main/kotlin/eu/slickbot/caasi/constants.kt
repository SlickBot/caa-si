package eu.slickbot.caasi

import org.maplibre.spatialk.geojson.Position

const val APP_NAME = "CAA-SI"

const val API_ID_URL = "https://uas-geo.caa.si"
const val API_BASE_URL = "https://caa-slovenia.maps.arcgis.com/sharing/rest"
const val API_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0"

const val API_CONNECT_TIMEOUT = 10L
const val API_READ_TIMEOUT = 30L
const val API_WRITE_TIMEOUT = 10L

const val PREFS_NAME = "$APP_NAME-${BuildConfig.BUILD_TYPE}.db"
const val DATABASE_NAME = "$APP_NAME-${BuildConfig.BUILD_TYPE}.pref"

val DEFAULT_CAMERA_LOCATION = Position(longitude = 15.026461780, latitude = 45.90136720)
const val DEFAULT_CAMERA_ZOOM = 7.3f

const val MAP_STYLE_LIGHT = "https://tiles.openfreemap.org/styles/liberty"
const val MAP_STYLE_DARK = "https://tiles.openfreemap.org/styles/dark"

const val MAP_STYLE_SATELLITE_JSON = """
{
  "version": 8,
  "sources": {
    "satellite": {
      "type": "raster",
      "tiles": ["https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"],
      "tileSize": 256,
      "maxzoom": 19,
      "attribution": "Esri, Maxar, Earthstar Geographics, and the GIS User Community"
    }
  },
  "layers": [
    { "id": "satellite", "type": "raster", "source": "satellite" }
  ]
}
"""
