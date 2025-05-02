package eu.slickbot.caasi

import com.google.android.gms.maps.model.LatLng

const val APP_NAME = "CAA-SI"

const val API_ID_URL = "https://uas-geo.caa.si"
const val API_BASE_URL = "https://caa-slovenia.maps.arcgis.com/sharing/rest"
const val API_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0"

const val API_CONNECT_TIMEOUT = 10L
const val API_READ_TIMEOUT = 30L
const val API_WRITE_TIMEOUT = 10L

const val PREFS_NAME = "$APP_NAME-${BuildConfig.BUILD_TYPE}.db"
const val DATABASE_NAME = "$APP_NAME-${BuildConfig.BUILD_TYPE}.pref"

val DEFAULT_CAMERA_LOCATION = LatLng(45.90136720, 15.026461780)
const val DEFAULT_CAMERA_ZOOM = 7.3f
