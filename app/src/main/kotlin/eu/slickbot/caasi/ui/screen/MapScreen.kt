package eu.slickbot.caasi.ui.screen

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.DomainDisabled
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eu.slickbot.caasi.DEFAULT_CAMERA_LOCATION
import eu.slickbot.caasi.DEFAULT_CAMERA_ZOOM
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.MapFeature
import eu.slickbot.caasi.data.api.model.zoneColor
import eu.slickbot.caasi.data.prefs.MapTheme
import eu.slickbot.caasi.data.repo.CaaSiRepository
import eu.slickbot.caasi.ui.component.DebugConsole
import eu.slickbot.caasi.ui.component.IconFab
import eu.slickbot.caasi.ui.component.LinearLoader
import eu.slickbot.caasi.ui.component.bottomsheet.ZoneDetailsSheet
import eu.slickbot.caasi.ui.component.dialog.LayersDialog
import eu.slickbot.caasi.ui.component.dialog.ThemeDialog
import eu.slickbot.caasi.ui.component.drawer.AppDrawer
import eu.slickbot.caasi.ui.component.map.Map
import eu.slickbot.caasi.ui.component.map.animateTo
import eu.slickbot.caasi.ui.component.map.lineFeatureCollection
import eu.slickbot.caasi.ui.component.map.pointFeatureCollection
import eu.slickbot.caasi.ui.component.map.polygonFeatureCollection
import eu.slickbot.caasi.ui.component.map.rememberMapCameraState
import eu.slickbot.caasi.ui.permission.LocationPrompt
import eu.slickbot.caasi.ui.permission.nextLocationPrompt
import eu.slickbot.caasi.ui.permission.rememberLocationPermissions
import eu.slickbot.caasi.ui.permission.shouldOfferPreciseUpgrade
import eu.slickbot.caasi.utils.circlePolygon
import eu.slickbot.caasi.utils.distanceToPolyline
import eu.slickbot.caasi.utils.pointInPolygon
import eu.slickbot.caasi.utils.rememberFusedLocationProviderClient
import eu.slickbot.caasi.utils.rememberLocationCallback
import eu.slickbot.caasi.utils.startLocationRequest
import eu.slickbot.caasi.utils.toPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
  vm: MapViewModel = koinViewModel(),
) {
  val activity = LocalActivity.current
  LaunchedEffect(activity) {
    vm.toastEvents.collectLatest {
      Toast.makeText(activity, it.message, it.duration).show()
    }
  }

  val layers by vm.layers.collectAsState(emptyList())
  val selectedLayers by vm.selectedLayers.collectAsState(emptyList())
  val mapThemes by vm.mapThemes.collectAsState()
  val selectedMapTheme by vm.selectedMapTheme.collectAsState(MapTheme.SYSTEM)
  val allFeatures by vm.allFeatures.collectAsState(initial = emptyList())
  val isLoading by vm.isLoading.collectAsState(initial = false)
  val isDebugVisible by vm.isDebugVisible.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  var showLayersDialog by remember { mutableStateOf(false) }
  var showThemeDialog by remember { mutableStateOf(false) }

  fun openUrl(url: String) {
    runCatching {
      context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
  }

  fun openFromDrawer(show: () -> Unit) {
    scope.launch { drawerState.close() }
    show()
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    // Swipe-to-open conflicts with map pan/zoom, so only allow drawer gestures while it's
    // already open (keeps swipe-to-close). The menu button remains the way to open it.
    gesturesEnabled = drawerState.isOpen,
    drawerContent = {
      AppDrawer(
        isDebugVisible = isDebugVisible,
        onToggleDebug = vm::toggleDebug,
        onLayersClick = { openFromDrawer { showLayersDialog = true } },
        onThemeClick = { openFromDrawer { showThemeDialog = true } },
        onOpenUrl = ::openUrl,
      )
    },
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
      Content(
        contentPadding = contentPadding,
        snackbarHostState = snackbarHostState,
        layers = layers,
        selectedLayers = selectedLayers,
        allFeatures = allFeatures,
        isLoading = isLoading,
        isDebugVisible = isDebugVisible,
        selectedMapTheme = selectedMapTheme,
        onOpenDrawer = { scope.launch { drawerState.open() } },
        refresh = vm::refresh,
        loadMapThemes = vm::loadMapThemes,
        loadBuiltFeatures = vm::loadBuiltFeatures,
      )
    }
  }

  if (showLayersDialog) {
    LayersDialog(
      layers = layers,
      selectedLayers = selectedLayers,
      onLayerToggled = vm::toggleLayer,
      onDismissRequest = { showLayersDialog = false },
    )
  }

  if (showThemeDialog) {
    ThemeDialog(
      mapThemes = mapThemes,
      selectedMapTheme = selectedMapTheme,
      onMapThemeSelected = vm::selectMapTheme,
      onDismissRequest = { showThemeDialog = false },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
  contentPadding: PaddingValues,
  snackbarHostState: SnackbarHostState,
  layers: List<Layer>,
  selectedLayers: List<Layer>,
  allFeatures: List<MapFeature>,
  isLoading: Boolean,
  isDebugVisible: Boolean,
  selectedMapTheme: MapTheme,
  onOpenDrawer: () -> Unit,
  refresh: () -> Unit,
  loadMapThemes: () -> Unit,
  loadBuiltFeatures: (Float, BoundingBox?) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val cameraState = rememberMapCameraState(
    position = DEFAULT_CAMERA_LOCATION,
    zoom = DEFAULT_CAMERA_ZOOM,
  )

  val zoneDetailsSheetState = rememberModalBottomSheetState()
  var selectedZone by remember { mutableStateOf<MapFeature?>(null) }

  // Bottom -> top draw order: nuclear features drawn last (on top). Same list drives the tap hit-test.
  val orderedFeatures = remember(allFeatures, layers) {
    val nuclearLayerId = layers.firstOrNull { it.title.contains("nuclear", ignoreCase = true) }?.id
    allFeatures.sortedBy { if (it.layer.id == nuclearLayerId) 1 else 0 }
  }

  fun dismissZoneDetailsSheet() {
    scope.launch { zoneDetailsSheetState.hide() }.invokeOnCompletion {
      if (!zoneDetailsSheetState.isVisible) selectedZone = null
    }
  }

  LaunchedEffect(Unit) {
    refresh()
    loadMapThemes()
  }

  var viewport by remember { mutableStateOf<Pair<Float, BoundingBox?>>(0f to null) }
  LaunchedEffect(selectedLayers, viewport) {
    delay(500) // debounce
    loadBuiltFeatures(viewport.first, viewport.second)
  }

  val context = LocalContext.current
  var lastLocation by remember { mutableStateOf<Location?>(null) }
  val locationCallback = rememberLocationCallback { lastLocation = it.lastLocation }
  val locationProvider = rememberFusedLocationProviderClient()
  val permissionsState = rememberLocationPermissions()

  LaunchedEffect(permissionsState.hasLocationAccess) {
    if (permissionsState.hasLocationAccess) {
      context.startLocationRequest(locationProvider, locationCallback)
    } else {
      permissionsState.launchMultiplePermissionRequest()
    }
  }

  var pendingPrompt by remember { mutableStateOf<LocationPrompt?>(null) }
  var preciseUpgradeOffered by rememberSaveable { mutableStateOf(false) }

  fun onLocationClick() {
    val prompt = nextLocationPrompt(
      granted = permissionsState.hasLocationAccess,
      shouldShowRationale = permissionsState.shouldShowRationale,
    )
    if (prompt != null) {
      pendingPrompt = prompt
      return
    }
    scope.launch {
      lastLocation?.let { location ->
        cameraState.animateTo(location.toPosition(), 14f)
      }
    }
    val offerPrecise = shouldOfferPreciseUpgrade(
      hasLocationAccess = permissionsState.hasLocationAccess,
      hasPreciseLocation = permissionsState.hasPreciseLocation,
      alreadyOffered = preciseUpgradeOffered,
    )
    if (offerPrecise) {
      preciseUpgradeOffered = true
      scope.launch {
        val result = snackbarHostState.showSnackbar(
          message = "Showing approximate location",
          actionLabel = "Enable precise",
          duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) {
          permissionsState.launchMultiplePermissionRequest()
        }
      }
    }
  }

  Map(
    cameraState = cameraState,
    contentPadding = contentPadding,
    mapTheme = selectedMapTheme,
    onMapClick = { position ->
      findFeatureAt(position, orderedFeatures)?.let { selectedZone = it }
    },
    onCameraIdle = { z, b -> viewport = z to b },
    additionalContent = {
      LinearLoader(
        modifier = Modifier.align(Alignment.TopCenter),
        show = isLoading,
      )
      Surface(
        modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        tonalElevation = 2.dp,
        onClick = onOpenDrawer,
      ) {
        Icon(
          modifier = Modifier.padding(10.dp).size(24.dp),
          imageVector = Icons.Default.Menu,
          contentDescription = "Open menu",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      IconFab(
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        imageVector = if (lastLocation != null) Icons.Default.LocationSearching else Icons.Default.LocationDisabled,
        onClick = ::onLocationClick,
      )
      val builtZonesShown = viewport.first >= CaaSiRepository.LAYER_BUILT_ZOOM_THRESHOLD
      Column(
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
      ) {
        if (isDebugVisible) {
          DebugConsole(cameraState = cameraState)
        }
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
    },
    mapContent = {
      // Group by layer so each airspace layer batches into ONE source + fill/line layers
      // (was the shared-layerId FillManager batching; now the native source/layer idiom).
      orderedFeatures.groupBy { it.layer }.forEach { (layer, features) ->
        key(layer.id) {
          LayerFeatures(layer.id, layer.zoneColor(), features)
        }
      }
      // Drawn last so it sits on top of all zone layers.
      UserLocation(lastLocation)
    },
  )

  ZoneDetailsSheet(
    zone = selectedZone,
    sheetState = zoneDetailsSheetState,
    onDismissRequest = ::dismissZoneDetailsSheet,
  )

  pendingPrompt?.let { prompt ->
    LocationPermissionDialog(
      prompt = prompt,
      onDismiss = { pendingPrompt = null },
      onRequestPermission = {
        pendingPrompt = null
        permissionsState.launchMultiplePermissionRequest()
      },
      onOpenSettings = {
        pendingPrompt = null
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
          data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
      },
    )
  }
}

@Composable
private fun LocationPermissionDialog(
  prompt: LocationPrompt,
  onDismiss: () -> Unit,
  onRequestPermission: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  when (prompt) {
    LocationPrompt.RequestWithRationale -> AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Location needed") },
      text = { Text("CaaSI uses your location to show your position on the airspace map. It stays on your device.") },
      confirmButton = { TextButton(onClick = onRequestPermission) { Text("Grant") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Not now") } },
    )
    LocationPrompt.OfferSettings -> AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Location permission required") },
      text = { Text("Location permission was denied. Enable it in app settings to see your position on the map.") },
      confirmButton = { TextButton(onClick = onOpenSettings) { Text("Open Settings") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
  }
}

private fun findFeatureAt(
  point: Position,
  features: List<MapFeature>,
  lineToleranceDeg: Double = 0.0005,
): MapFeature? {
  // features are ordered bottom -> top; test top-most first
  for (mapFeature in features.asReversed()) {
    val polygons = mapFeature.feature.geometry.polygons.orEmpty()
    if (polygons.any { pointInPolygon(point, it) }) {
      return mapFeature
    }
    val line = mapFeature.feature.geometry.line
    if (line != null && distanceToPolyline(point, line) <= lineToleranceDeg) {
      return mapFeature
    }
  }
  return null
}

private val PositionConverter = TwoWayConverter<Position, AnimationVector2D>(
  convertToVector = { AnimationVector2D(it.latitude.toFloat(), it.longitude.toFloat()) },
  convertFromVector = { Position(longitude = it.v2.toDouble(), latitude = it.v1.toDouble()) },
)

@Composable
private fun UserLocation(location: Location?) {
  if (location == null) return

  val positionAnim = remember { Animatable(location.toPosition(), PositionConverter) }
  val radiusAnim = remember { Animatable(location.accuracy) }

  LaunchedEffect(location) {
    val target = location.toPosition()
    // Short ease so the dot tracks the fix crisply (location updates arrive ~every 2s).
    val spec = tween<Position>(durationMillis = 600, easing = LinearEasing)
    val specF = tween<Float>(durationMillis = 600, easing = LinearEasing)
    launch { positionAnim.animateTo(target, spec) }
    launch { radiusAnim.animateTo(location.accuracy, specF) }
  }

  val center = positionAnim.value
  val accuracyRing = circlePolygon(center, radiusAnim.value.toDouble())

  val accuracySource = rememberGeoJsonSource(GeoJsonData.Features(polygonFeatureCollection(listOf(accuracyRing))))
  val dotSource = rememberGeoJsonSource(GeoJsonData.Features(pointFeatureCollection(center)))

  val accent = Color(0xFF1A73E8)
  // Accuracy halo: geographic (meters) — fill + 2dp ring border.
  FillLayer(id = "user-accuracy", source = accuracySource, color = const(accent), opacity = const(0.15f))
  LineLayer(id = "user-accuracy-border", source = accuracySource, color = const(accent), width = const(2.dp))
  // Position dot: fixed screen size — pixel-based circle.
  CircleLayer(
    id = "user-dot",
    source = dotSource,
    radius = const(7.dp),
    color = const(accent),
    strokeColor = const(Color.White),
    strokeWidth = const(2.dp),
  )
}

@Composable
private fun LayerFeatures(
  layerId: String,
  color: Color,
  features: List<MapFeature>,
) {
  val polygonRings = features.flatMap { it.feature.geometry.polygons.orEmpty() }
  val lines = features.mapNotNull { it.feature.geometry.line }

  val polygonSource = rememberGeoJsonSource(GeoJsonData.Features(polygonFeatureCollection(polygonRings)))
  val lineSource = rememberGeoJsonSource(GeoJsonData.Features(lineFeatureCollection(lines)))

  FillLayer(
    id = "fill-$layerId",
    source = polygonSource,
    color = const(color),
    opacity = const(0.35f),
  )
  LineLayer(
    id = "fill-border-$layerId",
    source = polygonSource,
    color = const(color),
    width = const(2.dp),
  )
  LineLayer(
    id = "line-$layerId",
    source = lineSource,
    color = const(color),
    width = const(3.dp),
  )
}

