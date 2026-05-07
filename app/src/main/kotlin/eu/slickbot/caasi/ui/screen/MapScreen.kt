package eu.slickbot.caasi.ui.screen

import android.location.Location
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DomainDisabled
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import eu.slickbot.caasi.DEFAULT_CAMERA_LOCATION
import eu.slickbot.caasi.DEFAULT_CAMERA_ZOOM
import eu.slickbot.caasi.R
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.MapFeature
import eu.slickbot.caasi.data.api.model.zoneColor
import eu.slickbot.caasi.data.repo.CaaSiRepository
import eu.slickbot.caasi.ui.component.DebugConsole
import eu.slickbot.caasi.ui.component.IconFab
import eu.slickbot.caasi.ui.component.LinearLoader
import eu.slickbot.caasi.ui.component.SpeedDialFab
import eu.slickbot.caasi.ui.component.SpeedDialItem
import eu.slickbot.caasi.ui.component.bottomsheet.LayersSheet
import eu.slickbot.caasi.ui.component.bottomsheet.MapTypesSheet
import eu.slickbot.caasi.ui.component.bottomsheet.ZoneDetailsSheet
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
  val isDebugVisible by vm.isDebugVisible.collectAsState()

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
      isDebugVisible = isDebugVisible,
      toggleDebug = vm::toggleDebug,
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
  allFeatures: List<MapFeature>,
  isLoading: Boolean,
  isDebugVisible: Boolean,
  toggleDebug: () -> Unit,
  loadLayers: () -> Unit,
  loadMapTypes: () -> Unit,
  loadOtherFeatures: () -> Unit,
  loadBuiltFeatures: (Float, LatLngBounds?) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val cameraPositionState = rememberCameraPositionState(
    latLng = DEFAULT_CAMERA_LOCATION,
    zoom = DEFAULT_CAMERA_ZOOM,
  )

  val layersSheetState = rememberModalBottomSheetState()
  val mapTypesSheetState = rememberModalBottomSheetState()
  val zoneDetailsSheetState = rememberModalBottomSheetState()
  var showLayersSheet by remember { mutableStateOf(false) }
  var showMapTypesSheet by remember { mutableStateOf(false) }
  var selectedZone by remember { mutableStateOf<MapFeature?>(null) }

  fun dismissLayersSheet() {
    scope.launch { layersSheetState.hide() }.invokeOnCompletion {
      if (!layersSheetState.isVisible) showLayersSheet = false
    }
  }

  fun dismissMapTypesSheet() {
    scope.launch { mapTypesSheetState.hide() }.invokeOnCompletion {
      if (!mapTypesSheetState.isVisible) showMapTypesSheet = false
    }
  }

  fun dismissZoneDetailsSheet() {
    scope.launch { zoneDetailsSheetState.hide() }.invokeOnCompletion {
      if (!zoneDetailsSheetState.isVisible) selectedZone = null
    }
  }

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
      LinearLoader(
        modifier = Modifier.align(Alignment.TopCenter),
        show = isLoading,
      )
      var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
      SpeedDialFab(
        modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        expanded = fabMenuExpanded,
        onExpandedChange = { fabMenuExpanded = it },
        icon = Icons.Default.Add,
        items = listOf(
          SpeedDialItem(Icons.Default.Layers, "Layers") {
            showLayersSheet = true
          },
          SpeedDialItem(Icons.Default.Map, "Map") {
            showMapTypesSheet = true
          },
          SpeedDialItem(Icons.Default.BugReport, if (isDebugVisible) "Hide debug" else "Show debug") {
            toggleDebug()
          },
        ),
      )
      Column(
        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        IconFab(
          imageVector = if (lastLocation != null) Icons.Default.LocationSearching else Icons.Default.LocationDisabled,
          onClick = ::onLocationClick,
        )
      }
      val builtZonesShown = cameraPositionState.position.zoom >= CaaSiRepository.LAYER_BUILT_ZOOM_THRESHOLD
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(16.dp),
        contentAlignment = Alignment.Center,
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
          tonalElevation = 2.dp,
        ) {
          Icon(
            modifier = Modifier.padding(8.dp).size(20.dp),
            imageVector = if (builtZonesShown) Icons.Default.Apartment else Icons.Default.DomainDisabled,
            contentDescription = if (builtZonesShown) "Built zones shown" else "Built zones hidden",
            tint = if (builtZonesShown) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          )
        }
      }
      if (isDebugVisible) {
        DebugConsole(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 32.dp)
            .padding(8.dp),
          cameraPositionState = cameraPositionState,
        )
      }
    },
    mapContent = {
      UserLocation(lastLocation)
      val zByLayer = layers
        .withIndex()
        .associate { (index, layer) -> layer.id to index.toFloat() }
      val nuclearLayerId = layers.firstOrNull { it.title.contains("nuclear", ignoreCase = true) }?.id
      for (mapFeature in allFeatures) {
        val baseZ = zByLayer[mapFeature.layer.id] ?: 0f
        val z = if (mapFeature.layer.id == nuclearLayerId) baseZ + 1000f else baseZ
        FeaturePolygons(mapFeature, z) { selectedZone = it }
        FeaturePolyline(mapFeature, z) { selectedZone = it }
      }
    },
  )

  LayersSheet(
    show = showLayersSheet,
    sheetState = layersSheetState,
    layers = layers,
    selectedLayers = selectedLayers,
    onLayerToggled = toggleLayer,
    onDismissRequest = ::dismissLayersSheet,
  )

  MapTypesSheet(
    show = showMapTypesSheet,
    sheetState = mapTypesSheetState,
    mapTypes = mapTypes,
    selectedMapType = selectedMapType,
    onMapTypeSelected = selectMapType,
    onDismissRequest = ::dismissMapTypesSheet,
  )

  ZoneDetailsSheet(
    zone = selectedZone,
    sheetState = zoneDetailsSheetState,
    onDismissRequest = ::dismissZoneDetailsSheet,
  )
}

private val LatLngConverter = TwoWayConverter<LatLng, AnimationVector2D>(
  convertToVector = { AnimationVector2D(it.latitude.toFloat(), it.longitude.toFloat()) },
  convertFromVector = { LatLng(it.v1.toDouble(), it.v2.toDouble()) },
)

@Composable
private fun UserLocation(location: Location?) {
  if (location == null) return

  val latLngAnim = remember { Animatable(location.toLatLng(), LatLngConverter) }
  val radiusAnim = remember { Animatable(location.accuracy) }

  LaunchedEffect(location) {
    val target = location.toLatLng()
    val spec = tween<LatLng>(durationMillis = 2000, easing = LinearEasing)
    val specF = tween<Float>(durationMillis = 2000, easing = LinearEasing)
    launch { latLngAnim.animateTo(target, spec) }
    launch { radiusAnim.animateTo(location.accuracy, specF) }
  }

  Marker(
    state = rememberUpdatedMarkerState(latLngAnim.value),
    icon = bitmapDescriptor(R.drawable.ic_my_location),
    anchor = Offset(0.5f, 0.5f),
  )
  Circle(
    center = latLngAnim.value,
    radius = radiusAnim.value.toDouble(),
    fillColor = Color(0xFF1A73E8).copy(alpha = 0.15f),
    strokeColor = Color(0xFF1A73E8).copy(alpha = 0.4f),
    strokeWidth = 2f,
  )
}

@Composable
private fun FeaturePolygons(
  mapFeature: MapFeature,
  zIndex: Float,
  onClick: (MapFeature) -> Unit,
) {
  val baseColor = mapFeature.layer.zoneColor()
  val fill = baseColor.copy(alpha = 0.35f)
  for (polygon in mapFeature.feature.geometry.polygons.orEmpty()) {
    Polygon(
      points = polygon,
      fillColor = fill,
      strokeColor = baseColor,
      strokeWidth = 2f,
      clickable = true,
      onClick = { onClick(mapFeature) },
      zIndex = zIndex,
    )
  }
}

@Composable
private fun FeaturePolyline(
  mapFeature: MapFeature,
  zIndex: Float,
  onClick: (MapFeature) -> Unit,
) {
  val color = mapFeature.layer.zoneColor()
  mapFeature.feature.geometry.line?.let { polyline ->
    Polyline(
      points = polyline,
      color = color,
      width = 3f,
      clickable = true,
      onClick = { onClick(mapFeature) },
      zIndex = zIndex,
    )
  }
}
