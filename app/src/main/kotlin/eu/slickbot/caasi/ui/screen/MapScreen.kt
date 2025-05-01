package eu.slickbot.caasi.ui.screen

import android.location.Location
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.LayerFeature
import eu.slickbot.caasi.ui.component.DebugConsole
import eu.slickbot.caasi.ui.component.IconFab
import eu.slickbot.caasi.ui.component.LinearLoader
import eu.slickbot.caasi.ui.component.bottomsheet.LayersSheet
import eu.slickbot.caasi.ui.component.bottomsheet.MapTypesSheet
import eu.slickbot.caasi.ui.component.map.Map
import eu.slickbot.caasi.ui.component.map.animateTo
import eu.slickbot.caasi.ui.component.map.rememberCameraPositionState
import eu.slickbot.caasi.utils.bitmapDescriptor
import eu.slickbot.caasi.utils.rememberFusedLocationProviderClient
import eu.slickbot.caasi.utils.rememberLocationCallback
import eu.slickbot.caasi.utils.rememberLocationPermissions
import eu.slickbot.caasi.utils.startLocationRequest
import eu.slickbot.caasi.utils.toLatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MapScreen(
  vm: MapViewModel = koinInject(),
) {
  val activity = LocalActivity.current
  LaunchedEffect(activity) {
    vm.toastEvents.collectLatest {
      Toast.makeText(activity, it.message, it.duration).show()
    }
  }

  val layers by vm.layers.collectAsState()
  val selectedLayers by vm.selectedLayers.collectAsState(emptyList())
  val mapTypes by vm.mapTypes.collectAsState()
  val selectedMapType by vm.selectedMapType.collectAsState(MapType.NORMAL)
  val allFeatures by vm.allFeatures.collectAsState(initial = emptyList())
  val isLoading by vm.isLoading.collectAsState(initial = false)

  Scaffold(modifier = Modifier.fillMaxSize()) { contentPadding ->
    Content(
      contentPadding = contentPadding,
      layers = layers,
      selectedLayers = selectedLayers,
      toggleLayer = vm::toggleLayer,
      mapTypes = mapTypes,
      selectedMapType = selectedMapType,
      selectMapType = vm::selectMapType,
      allFeatures = allFeatures,
      isLoading = isLoading,
      loadLayers = vm::loadLayers,
      loadMapTypes = vm::loadMapTypes,
      loadOtherFeatures = vm::loadOtherFeatures,
      loadBuiltFeatures = vm::loadBuiltFeatures,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun Content(
  contentPadding: PaddingValues,
  layers: List<Layer>,
  selectedLayers: List<Layer>,
  toggleLayer: (Layer, Boolean) -> Unit,
  mapTypes: List<MapType>,
  selectedMapType: MapType,
  selectMapType: (MapType) -> Unit,
  allFeatures: List<LayerFeature>,
  isLoading: Boolean,
  loadLayers: () -> Unit,
  loadMapTypes: () -> Unit,
  loadOtherFeatures: () -> Unit,
  loadBuiltFeatures: (Float, LatLngBounds?) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val cameraPositionState = rememberCameraPositionState(
    latLng = LatLng(45.90136720, 15.026461780),
    zoom = 7.3f,
  )

  val layersSheetState = rememberModalBottomSheetState()
  val mapTypesSheetState = rememberModalBottomSheetState()

  LaunchedEffect(Unit) {
    loadLayers()
    loadMapTypes()
  }

  LaunchedEffect(selectedLayers) {
    loadOtherFeatures()
  }

  val zoom = cameraPositionState.position.zoom
  val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
  LaunchedEffect(selectedLayers, zoom, bounds) {
    delay(500) // debounce
    loadBuiltFeatures(zoom, bounds)
  }

  val context = LocalContext.current
  var lastLocation by remember { mutableStateOf<Location?>(null) }
  val locationCallback = rememberLocationCallback { lastLocation = it.lastLocation }
  val locationProvider = rememberFusedLocationProviderClient()
  val permissionsState = rememberLocationPermissions()

  LaunchedEffect(permissionsState.allPermissionsGranted) {
    if (permissionsState.allPermissionsGranted) {
      context.startLocationRequest(locationProvider, locationCallback)
    } else {
      permissionsState.launchMultiplePermissionRequest()
    }
  }

  fun onLocationClick() {
    scope.launch {
      if (permissionsState.allPermissionsGranted) {
        lastLocation?.let { location ->
          cameraPositionState.animateTo(location.toLatLng(), 14f)
        }
      } else {
        permissionsState.launchMultiplePermissionRequest()
      }
    }
  }

  Map(
    contentPadding = contentPadding,
    cameraPositionState = cameraPositionState,
    mapType = selectedMapType,
    additionalContent = {
      Column(
        modifier = Modifier.align(Alignment.TopStart),
      ) {
        LinearLoader(show = isLoading)
        DebugConsole(
          modifier = Modifier.padding(top = 10.dp, start = 16.dp),
          cameraPositionState = cameraPositionState,
        )
      }
      Column(
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        IconFab(
          imageVector = Icons.Default.Layers,
          onClick = { scope.launch { layersSheetState.show() } },
        )
        IconFab(
          imageVector = Icons.Default.Map,
          onClick = { scope.launch { mapTypesSheetState.show() } },
        )
      }
      Column(
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        IconFab(
          imageVector = if (lastLocation != null) Icons.Default.LocationSearching else Icons.Default.LocationDisabled,
          onClick = ::onLocationClick,
        )
      }
    },
    mapContent = {
      UserLocation(lastLocation)
      for (feature in allFeatures) {
        FeaturePolygons(feature)
        FeaturePolyline(feature)
      }
    },
  )

  LayersSheet(
    sheetState = layersSheetState,
    layers = layers,
    selectedLayers = selectedLayers,
    onLayerToggled = toggleLayer,
    onDismissRequest = { scope.launch { layersSheetState.hide() } },
  )

  MapTypesSheet(
    sheetState = mapTypesSheetState,
    mapTypes = mapTypes,
    selectedMapType = selectedMapType,
    onMapTypeSelected = selectMapType,
    onDismissRequest = { scope.launch { mapTypesSheetState.hide() } },
  )
}

@Composable
private fun UserLocation(location: Location?) {
  val latLng = remember(location) { location?.toLatLng() }
  val radius = remember(location) { location?.accuracy?.toDouble() }
  if (latLng != null) {
    Marker(
      state = rememberUpdatedMarkerState(latLng),
      icon = bitmapDescriptor(android.R.drawable.star_big_on),
      anchor = Offset(0.5f, 0.5f),
    )
    if (radius != null) {
      Circle(
        center = latLng,
        radius = radius,
        strokeColor = Color.Red,
        strokeWidth = 3f,
      )
    }
  }
}

@Composable
private fun FeaturePolygons(
  feature: LayerFeature,
) {
  for (polygon in feature.geometry.polygons.orEmpty()) {
    Polygon(
      points = polygon,
      fillColor = feature.color,
      strokeWidth = 2f,
//      clickable = true,
//      onClick = { onFeatureClick(element.layer, element.feature) },
    )
  }
}

@Composable
private fun FeaturePolyline(
  feature: LayerFeature,
) {
  feature.geometry.line?.let { polyline ->
    Polyline(
      points = polyline,
      color = feature.color,
      width = 2f,
//      clickable = true,
//      onClick = { onFeatureClick(element.layer, element.feature) },
    )
  }
}
