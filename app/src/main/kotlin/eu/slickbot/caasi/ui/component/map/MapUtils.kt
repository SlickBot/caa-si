package eu.slickbot.caasi.ui.component.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberMapCameraState(
  position: Position,
  zoom: Float,
): CameraState {
  return rememberCameraState(
    firstPosition = CameraPosition(target = position, zoom = zoom.toDouble()),
  )
}

@Composable
fun rememberMapOptions(
  topInset: Dp = 0.dp,
  rotationGesturesEnabled: Boolean = false,
  scrollGesturesEnabled: Boolean = true,
  tiltGesturesEnabled: Boolean = true,
  zoomGesturesEnabled: Boolean = true,
): MapOptions {
  val base = 8.dp
  return MapOptions(
    gestureOptions = GestureOptions(
      isRotateEnabled = rotationGesturesEnabled,
      isScrollEnabled = scrollGesturesEnabled,
      isTiltEnabled = tiltGesturesEnabled,
      isZoomEnabled = zoomGesturesEnabled,
    ),
    ornamentOptions = OrnamentOptions(
      isLogoEnabled = false,
      isAttributionEnabled = false,
      isScaleBarEnabled = false,
      isCompassEnabled = true,
      compassAlignment = Alignment.TopEnd,
      padding = PaddingValues(top = topInset + base, end = base, start = base, bottom = base),
    ),
  )
}

suspend fun CameraState.animateTo(
  target: Position,
  zoom: Float,
  tilt: Float = 0f,
  bearing: Float = 0f,
  durationMs: Int = 1000,
) {
  animateTo(
    finalPosition = position.copy(
      target = target,
      zoom = zoom.toDouble(),
      tilt = tilt.toDouble(),
      bearing = bearing.toDouble(),
    ),
    duration = durationMs.milliseconds,
  )
}
