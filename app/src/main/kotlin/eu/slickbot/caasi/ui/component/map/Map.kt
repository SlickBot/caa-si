package eu.slickbot.caasi.ui.component.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import eu.slickbot.caasi.R
import eu.slickbot.caasi.ui.component.StatusAndNavigationBars
import eu.slickbot.caasi.utils.takeIf

@Composable
fun Map(
  modifier: Modifier = Modifier,
  mapType: MapType = MapType.NORMAL,
  darkTheme: Boolean = isSystemInDarkTheme(),
  contentPadding: PaddingValues = PaddingValues(),
  cameraPositionState: CameraPositionState = rememberCameraPositionState(),
  handleStatusAndNavigationBars: Boolean = true,
  additionalContent: @Composable BoxScope.() -> Unit = {},
  mapContent: @Composable @GoogleMapComposable () -> Unit = {},
) {
  val context = LocalContext.current
  val mapProperties = rememberMapProperties(
    mapType = mapType,
    mapStyleOptions = takeIf(darkTheme && mapType == MapType.NORMAL) {
      MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark)
    }
  )
  val mapUiSettings = rememberMapUiSettings(
    zoomControlsEnabled = false,
    indoorLevelPickerEnabled = false,
  )
  val darkStyle = remember(mapType, darkTheme) {
    when (mapType) {
      MapType.NONE -> false
      MapType.NORMAL -> darkTheme
      MapType.SATELLITE -> true
      MapType.TERRAIN -> false
      MapType.HYBRID -> true
    }
  }

  if (handleStatusAndNavigationBars) {
    StatusAndNavigationBars(darkTheme = darkStyle)
  }

  Box(modifier = modifier) {
    GoogleMap(
      cameraPositionState = cameraPositionState,
      properties = mapProperties,
      uiSettings = mapUiSettings,
      contentPadding = contentPadding,
    ) {
      mapContent()
    }
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
      additionalContent()
    }
  }
}
