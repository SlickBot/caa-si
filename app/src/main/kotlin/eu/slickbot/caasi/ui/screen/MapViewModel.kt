package eu.slickbot.caasi.ui.screen

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType
import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.LayerFeature
import eu.slickbot.caasi.utils.asyncFlatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
  private val api: CaaSiApi,
) : ViewModel() {

  private val _isLoadingLayers = MutableStateFlow(false)
  val isLoadingLayers = _isLoadingLayers.asStateFlow()

  private val _isLoadingFeaturesBuilt = MutableStateFlow(false)
  val isLoadingFeaturesBuilt = _isLoadingFeaturesBuilt.asStateFlow()

  private val _isLoadingFeaturesOther = MutableStateFlow(false)
  val isLoadingFeaturesOther = _isLoadingFeaturesOther.asStateFlow()

  val isLoading: Flow<Boolean> = combine(
    isLoadingLayers,
    isLoadingFeaturesBuilt,
    isLoadingFeaturesOther,
  ) { it.any { it } }

  private val _layers = MutableStateFlow<List<Layer>>(emptyList())
  val layers = _layers.asStateFlow()

  private val _selectedLayers = MutableStateFlow<List<Layer>>(emptyList())
  val selectedLayers = _selectedLayers.asStateFlow()

  private val _mapTypes = MutableStateFlow<List<MapType>>(emptyList())
  val mapTypes = _mapTypes.asStateFlow()

  private val _selectedMapType = MutableStateFlow<MapType>(MapType.NORMAL)
  val selectedMapType = _selectedMapType.asStateFlow()

  private val _featuresBuilt = MutableStateFlow<List<LayerFeature>>(emptyList())
  val featuresBuilt = _featuresBuilt.asStateFlow()

  private val _featuresOther = MutableStateFlow<List<LayerFeature>>(emptyList())
  val featuresOther = _featuresOther.asStateFlow()

  val allFeatures: Flow<List<LayerFeature>> = combine(
    featuresBuilt,
    featuresOther,
  ) { built, other -> built + other }

  private val _toastEvents = Channel<ToastEvent>()
  val toastEvents = _toastEvents.receiveAsFlow()

  fun loadLayers() {
    viewModelScope.launch {
      _isLoadingLayers.value = true
      withContext(Dispatchers.IO) {
        runCatching { api.getLayers() }.fold(
          onSuccess = { layers ->
            _layers.value = layers
            _selectedLayers.value = layers
          },
          onFailure = { th ->
            _toastEvents.send(ToastEvent("Failed to load Layers: ${th.toString()}"))
          },
        )
      }
      _isLoadingLayers.value = false
    }
  }

  fun loadMapTypes() {
    viewModelScope.launch {
      _mapTypes.value = MapType.entries - MapType.NONE
    }
  }

  fun toggleLayer(layer: Layer, enabled: Boolean) {
    _selectedLayers.value = if (enabled) {
      _selectedLayers.value + layer
    } else {
      _selectedLayers.value - layer
    }
  }

  fun selectMapType(mapType: MapType) {
    _selectedMapType.value = mapType
  }

  fun loadBuiltFeatures(zoom: Float, bounds: LatLngBounds?) {
    if (zoom < LAYER_BUILT_ZOOM_THRESHOLD || bounds == null) {
      _featuresBuilt.value = emptyList()
      return
    }

    viewModelScope.launch {
      _isLoadingFeaturesBuilt.value = true
      runCatching {
        val builtLayers = withContext(Dispatchers.Default) {
          _selectedLayers.value.filter { it.title == LAYER_BUILT_TITLE }
        }
        withContext(Dispatchers.IO) {
          builtLayers.asyncFlatMap { api.getLayerFeatures(it, bounds) }
        }
      }.fold(
        onSuccess = { features ->
          _featuresBuilt.value = features
        },
        onFailure = { th ->
          _toastEvents.send(ToastEvent("Failed to load Built Features: ${th.toString()}"))
        },
      )
      _isLoadingFeaturesBuilt.value = false
    }
  }

  fun loadOtherFeatures() {
    viewModelScope.launch {
      _isLoadingFeaturesOther.value = true
      runCatching {
        val otherLayers = withContext(Dispatchers.Default) {
          _selectedLayers.value.filter { it.title != LAYER_BUILT_TITLE }
        }
        withContext(Dispatchers.IO) {
          otherLayers.asyncFlatMap { api.getLayerFeatures(it) }
        }
      }.fold(
        onSuccess = { features ->
          _featuresOther.value = features
        },
        onFailure = { th ->
          _toastEvents.send(ToastEvent("Failed to load Other Features: ${th.toString()}"))
        },
      )
      _isLoadingFeaturesOther.value = false
    }
  }

  companion object {
    private const val LAYER_BUILT_ZOOM_THRESHOLD = 12f
    private const val LAYER_BUILT_TITLE = "Pozidano-built"
  }

  data class ToastEvent(
    val message: String,
    val duration: Int = Toast.LENGTH_LONG,
  )

}
