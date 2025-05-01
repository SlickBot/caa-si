package eu.slickbot.caasi.ui.component.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.lang.Integer.MAX_VALUE

@Composable
fun rememberMapUiSettings(
  compassEnabled: Boolean = true,
  indoorLevelPickerEnabled: Boolean = true,
  mapToolbarEnabled: Boolean = true,
  myLocationButtonEnabled: Boolean = true,
  rotationGesturesEnabled: Boolean = true,
  scrollGesturesEnabled: Boolean = true,
  scrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
  tiltGesturesEnabled: Boolean = true,
  zoomControlsEnabled: Boolean = true,
  zoomGesturesEnabled: Boolean = true,
): MapUiSettings {
  return remember(
    compassEnabled,
    indoorLevelPickerEnabled,
    mapToolbarEnabled,
    myLocationButtonEnabled,
    rotationGesturesEnabled,
    scrollGesturesEnabled,
    scrollGesturesEnabledDuringRotateOrZoom,
    tiltGesturesEnabled,
    zoomControlsEnabled,
    zoomGesturesEnabled,
  ) {
    MapUiSettings(
      compassEnabled = compassEnabled,
      indoorLevelPickerEnabled = indoorLevelPickerEnabled,
      mapToolbarEnabled = mapToolbarEnabled,
      myLocationButtonEnabled = myLocationButtonEnabled,
      rotationGesturesEnabled = rotationGesturesEnabled,
      scrollGesturesEnabled = scrollGesturesEnabled,
      scrollGesturesEnabledDuringRotateOrZoom = scrollGesturesEnabledDuringRotateOrZoom,
      tiltGesturesEnabled = tiltGesturesEnabled,
      zoomControlsEnabled = zoomControlsEnabled,
      zoomGesturesEnabled = zoomGesturesEnabled,
    )
  }
}

@Composable
fun rememberCameraPositionState(
  latLng: LatLng,
  zoom: Float,
): CameraPositionState {
  return rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(latLng, zoom)
  }
}

@Composable
fun rememberMapProperties(
  isBuildingEnabled: Boolean = false,
  isIndoorEnabled: Boolean = false,
  isMyLocationEnabled: Boolean = false,
  isTrafficEnabled: Boolean = false,
  latLngBoundsForCameraTarget: LatLngBounds? = null,
  mapStyleOptions: MapStyleOptions? = null,
  mapType: MapType = MapType.NORMAL,
  maxZoomPreference: Float = 21.0f,
  minZoomPreference: Float = 3.0f,
): MapProperties {
  return remember(
    isBuildingEnabled,
    isIndoorEnabled,
    isMyLocationEnabled,
    isTrafficEnabled,
    latLngBoundsForCameraTarget,
    mapStyleOptions,
    mapType,
    maxZoomPreference,
    minZoomPreference,
  ) {
    MapProperties(
      isBuildingEnabled = isBuildingEnabled,
      isIndoorEnabled = isIndoorEnabled,
      isMyLocationEnabled = isMyLocationEnabled,
      isTrafficEnabled = isTrafficEnabled,
      latLngBoundsForCameraTarget = latLngBoundsForCameraTarget,
      mapStyleOptions = mapStyleOptions,
      mapType = mapType,
      maxZoomPreference = maxZoomPreference,
      minZoomPreference = minZoomPreference,
    )
  }
}

suspend fun CameraPositionState.animateTo(
  target: LatLng,
  zoom: Float,
  tilt: Float = 0f,
  bearing: Float = 0f,
  durationMs: Int = MAX_VALUE,
) {
  animate(
    update = CameraUpdateFactory.newCameraPosition(CameraPosition(target, zoom, tilt, bearing)),
    durationMs = durationMs,
  )
}
