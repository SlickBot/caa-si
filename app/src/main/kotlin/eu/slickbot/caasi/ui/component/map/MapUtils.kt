package eu.slickbot.caasi.ui.component.map

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.CameraMotionType
import org.ramani.compose.CameraPosition
import org.ramani.compose.CameraPositionState
import org.ramani.compose.MapProperties
import org.ramani.compose.Margins
import org.ramani.compose.UiSettings
import org.ramani.compose.rememberCameraPositionState

@Composable
fun rememberCameraPositionState(
  latLng: LatLng,
  zoom: Float,
): CameraPositionState {
  return rememberCameraPositionState(
    CameraPosition(target = latLng, zoom = zoom.toDouble()),
  )
}

@Composable
fun rememberMapUiSettings(
  topInsetPx: Int = 0,
  bottomInsetPx: Int = 0,
  rotationGesturesEnabled: Boolean = true,
  scrollGesturesEnabled: Boolean = true,
  tiltGesturesEnabled: Boolean = true,
  zoomGesturesEnabled: Boolean = true,
): UiSettings {
  val base = with(LocalDensity.current) { 8.dp.roundToPx() }
  return UiSettings(
    isLogoEnabled = false,
    isAttributionEnabled = true,
    // Offset MapLibre's native compass (top) and attribution (bottom) past the system bars.
    compassGravity = Gravity.TOP or Gravity.END,
    compassMargins = Margins(left = base, top = topInsetPx + base, right = base, bottom = base),
    attributionsMargins = Margins(left = base, top = base, right = base, bottom = bottomInsetPx + base),
    rotateGesturesEnabled = rotationGesturesEnabled,
    scrollGesturesEnabled = scrollGesturesEnabled,
    tiltGesturesEnabled = tiltGesturesEnabled,
    zoomGesturesEnabled = zoomGesturesEnabled,
  )
}

@Composable
fun rememberMapProperties(
  maxZoomPreference: Double = 21.0,
  minZoomPreference: Double = 3.0,
): MapProperties {
  return MapProperties(
    maxZoom = maxZoomPreference,
    minZoom = minZoomPreference,
  )
}

fun CameraPositionState.animateTo(
  target: LatLng,
  zoom: Float,
  tilt: Float = 0f,
  bearing: Float = 0f,
  durationMs: Int = 1000,
) {
  position = CameraPosition(
    target = target,
    zoom = zoom.toDouble(),
    tilt = tilt.toDouble(),
    bearing = bearing.toDouble(),
    motionType = CameraMotionType.EASE,
    animationDurationMs = durationMs,
  )
}
