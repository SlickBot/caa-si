package eu.slickbot.caasi.ui.component.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import eu.slickbot.caasi.MAP_STYLE_DARK
import eu.slickbot.caasi.MAP_STYLE_LIGHT
import eu.slickbot.caasi.MAP_STYLE_SATELLITE_JSON
import eu.slickbot.caasi.data.prefs.MapTheme
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.ramani.compose.CameraPositionState
import org.ramani.compose.MapLibre
import org.ramani.compose.MapStyle
import org.ramani.compose.rememberCameraPositionState
import org.ramani.compose.rememberMapViewWithLifecycle

@Composable
fun Map(
  modifier: Modifier = Modifier,
  mapTheme: MapTheme = MapTheme.SYSTEM,
  systemDarkTheme: Boolean = isSystemInDarkTheme(),
  cameraPositionState: CameraPositionState = rememberCameraPositionState(),
  contentPadding: PaddingValues = PaddingValues(),
  onMapClick: (LatLng) -> Unit = {},
  onCameraIdle: (zoom: Float, bounds: LatLngBounds) -> Unit = { _, _ -> },
  additionalContent: @Composable BoxScope.() -> Unit = {},
  mapContent: @Composable () -> Unit = {},
) {
  val style = when (mapTheme) {
    MapTheme.SATELLITE -> MapStyle.Json(MAP_STYLE_SATELLITE_JSON)
    MapTheme.LIGHT -> MapStyle.Uri(MAP_STYLE_LIGHT)
    MapTheme.DARK -> MapStyle.Uri(MAP_STYLE_DARK)
    MapTheme.SYSTEM -> MapStyle.Uri(if (systemDarkTheme) MAP_STYLE_DARK else MAP_STYLE_LIGHT)
  }
  val mapView: MapView = rememberMapViewWithLifecycle()
  val density = LocalDensity.current
  val topInsetPx = with(density) { contentPadding.calculateTopPadding().roundToPx() }
  val bottomInsetPx = with(density) { contentPadding.calculateBottomPadding().roundToPx() }
  val uiSettings = rememberMapUiSettings(
    topInsetPx = topInsetPx,
    bottomInsetPx = bottomInsetPx,
    rotationGesturesEnabled = false,
  )
  val properties = rememberMapProperties()
  // Guard so the camera-idle listener is registered only once across recompositions.
  val idleListenerAdded = remember { booleanArrayOf(false) }
  val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)

  Box(modifier = modifier) {
    MapLibre(
      modifier = Modifier.fillMaxSize(),
      style = style,
      cameraPositionState = cameraPositionState,
      uiSettings = uiSettings,
      properties = properties,
      mapView = mapView,
      onMapClick = onMapClick,
      onStyleLoaded = { _ ->
        if (!idleListenerAdded[0]) {
          idleListenerAdded[0] = true
          mapView.getMapAsync { map ->
            // Suppress ramani's built-in location puck: it renders beneath the annotation
            // layers and would duplicate our own on-top location indicator (drawn in mapContent).
            // ramani activates it after this callback, so disable it immediately, again after a
            // short delay (to win the race), and re-assert on every camera idle.
            val disablePuck = Runnable {
              runCatching { map.locationComponent.isLocationComponentEnabled = false }
            }
            disablePuck.run()
            mapView.postDelayed(disablePuck, 1500)
            map.addOnCameraIdleListener {
              disablePuck.run()
              val region = map.projection.visibleRegion
              currentOnCameraIdle(map.cameraPosition.zoom.toFloat(), region.latLngBounds)
            }
          }
        }
      },
      content = { mapContent() },
    )
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
      additionalContent()
    }
  }
}
