package eu.slickbot.caasi.ui.screen

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.MapFeature
import eu.slickbot.caasi.data.repo.CaaSiRepository
import eu.slickbot.caasi.utils.foldSafe
import eu.slickbot.caasi.utils.toUserMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(
  private val repo: CaaSiRepository,
) : ViewModel() {

  private val _isLoadingLayers = MutableStateFlow(false)
  val isLoadingLayers = _isLoadingLayers.asStateFlow()

  private val _isLoadingFeaturesBuilt = MutableStateFlow(false)
  val isLoadingFeaturesBuilt = _isLoadingFeaturesBuilt.asStateFlow()

  val isLoading: Flow<Boolean> = combine(
    isLoadingLayers,
    isLoadingFeaturesBuilt,
  ) { a, b -> a || b }

  val layers: Flow<List<Layer>> = repo.layersFlow()
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  private val _isDebugVisible = MutableStateFlow(false)
  val isDebugVisible = _isDebugVisible.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val selectedLayers: Flow<List<Layer>> = layers.flatMapMerge { repo.getSelectedLayers(it) }

  private val _mapTypes = MutableStateFlow<List<MapType>>(emptyList())
  val mapTypes = _mapTypes.asStateFlow()

  val selectedMapType = repo.getSelectedMapType()

  private val _featuresBuilt = MutableStateFlow<List<MapFeature>>(emptyList())
  val featuresBuilt = _featuresBuilt.asStateFlow()

  // Cached features from DB, filtered by selected layers (BUILT is never in cache)
  val featuresOther: Flow<List<MapFeature>> = combine(
    repo.mapFeaturesFlow(),
    selectedLayers,
  ) { all, selected ->
    val selectedIds = selected.map { it.id }.toSet()
    all.filter { it.layer.id in selectedIds }
  }

  val allFeatures: Flow<List<MapFeature>> = combine(
    featuresBuilt,
    featuresOther,
  ) { built, other -> built + other }

  private val _toastEvents = Channel<ToastEvent>()
  val toastEvents = _toastEvents.receiveAsFlow()

  private var refreshJob: Job? = null
  private var featuresBuiltJob: Job? = null

  fun refresh() {
    refreshJob?.cancel()
    refreshJob = viewModelScope.launch {
      _isLoadingLayers.value = true
      runCatching {
        repo.refreshAll()
      }.foldSafe(
        onSuccess = { /* DB Flow propagates */ },
        onFailure = { th ->
          _toastEvents.send(ToastEvent(th.toUserMessage("Failed to refresh data")))
        },
      )
      _isLoadingLayers.value = false
    }
  }

  fun loadMapTypes() {
    _mapTypes.value = MapType.entries - MapType.NONE
  }

  fun toggleDebug() {
    _isDebugVisible.value = !_isDebugVisible.value
  }

  fun toggleLayer(layer: Layer, enabled: Boolean) {
    viewModelScope.launch {
      val selectedLayers = if (enabled) {
        selectedLayers.first() + layer
      } else {
        selectedLayers.first() - layer
      }
      repo.saveSelectedLayers(selectedLayers)
    }
  }

  fun selectMapType(mapType: MapType) {
    viewModelScope.launch {
      repo.saveSelectedMapType(mapType)
    }
  }

  fun loadBuiltFeatures(zoom: Float, bounds: LatLngBounds?) {
    featuresBuiltJob?.cancel()
    featuresBuiltJob = viewModelScope.launch {
      _isLoadingFeaturesBuilt.value = true
      runCatching {
        repo.getBuiltFeatures(selectedLayers.first(), bounds, zoom)
      }.foldSafe(
        onSuccess = { features ->
          _featuresBuilt.value = features
        },
        onFailure = { th ->
          _toastEvents.send(ToastEvent(th.toUserMessage("Failed to load Built Features")))
        },
      )
      _isLoadingFeaturesBuilt.value = false
    }
  }

  data class ToastEvent(
    val message: String,
    val duration: Int = Toast.LENGTH_LONG,
  )

}
