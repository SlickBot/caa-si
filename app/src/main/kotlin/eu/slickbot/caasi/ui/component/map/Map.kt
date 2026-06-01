package eu.slickbot.caasi.ui.component.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import eu.slickbot.caasi.MAP_STYLE_DARK
import eu.slickbot.caasi.MAP_STYLE_LIGHT
import eu.slickbot.caasi.MAP_STYLE_SATELLITE_JSON
import eu.slickbot.caasi.data.prefs.MapTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@OptIn(FlowPreview::class)
@Composable
fun Map(
  modifier: Modifier = Modifier,
  mapTheme: MapTheme = MapTheme.SYSTEM,
  systemDarkTheme: Boolean = isSystemInDarkTheme(),
  cameraState: CameraState = rememberMapCameraState(LatLng(0.0, 0.0), 0f),
  contentPadding: PaddingValues = PaddingValues(),
  onMapClick: (LatLng) -> Unit = {},
  onCameraIdle: (zoom: Float, bounds: LatLngBounds) -> Unit = { _, _ -> },
  additionalContent: @Composable BoxScope.() -> Unit = {},
  mapContent: @Composable () -> Unit = {},
) {
  val baseStyle = when (mapTheme) {
    MapTheme.SATELLITE -> BaseStyle.Json(MAP_STYLE_SATELLITE_JSON)
    MapTheme.LIGHT -> BaseStyle.Uri(MAP_STYLE_LIGHT)
    MapTheme.DARK -> BaseStyle.Uri(MAP_STYLE_DARK)
    MapTheme.SYSTEM -> BaseStyle.Uri(if (systemDarkTheme) MAP_STYLE_DARK else MAP_STYLE_LIGHT)
  }
  val topInset = contentPadding.calculateTopPadding()
  val options = rememberMapOptions(topInset = topInset, rotationGesturesEnabled = false)
  val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
  val currentOnMapClick by rememberUpdatedState(onMapClick)

  // Replaces ramani's addOnCameraIdleListener: emit (zoom, bounds) once the camera settles.
  LaunchedEffect(cameraState) {
    snapshotFlow { cameraState.position }
      .debounce(200L)
      .collectLatest {
        val zoom = cameraState.position.zoom.toFloat()
        val bounds = cameraState.projection?.queryVisibleBoundingBox()?.toLatLngBounds()
        if (bounds != null) currentOnCameraIdle(zoom, bounds)
      }
  }

  Box(modifier = modifier) {
    MaplibreMap(
      modifier = Modifier.fillMaxSize(),
      baseStyle = baseStyle,
      cameraState = cameraState,
      zoomRange = 3f..21f,
      options = options,
      onMapClick = { pos, _ ->
        currentOnMapClick(pos.toLatLng())
        ClickResult.Pass
      },
      content = { mapContent() },
    )
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
      additionalContent()
    }
  }
}
