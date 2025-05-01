package eu.slickbot.caasi.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPrefs(
  private val context: Context,
) {

  private val Context.dataStore by preferencesDataStore(name = "map_settings")

  val selectedLayerIdsFlow: Flow<Set<String>?> =
    context.dataStore.data.map {
      it[Keys.SELECTED_LAYERS]
    }

  val mapTypesFlow: Flow<String> =
    context.dataStore.data.map {
      it[Keys.MAP_TYPE] ?: MapType.NORMAL.name
    }

  suspend fun saveSelectedLayerIds(ids: Set<String>) {
    context.dataStore.edit {
      it[Keys.SELECTED_LAYERS] = ids
    }
  }

  suspend fun saveMapTypeId(id: String) {
    context.dataStore.edit {
      it[Keys.MAP_TYPE] = id
    }
  }

  private object Keys {
    val SELECTED_LAYERS = stringSetPreferencesKey("selected_layers")
    val MAP_TYPE = stringPreferencesKey("map_type")
  }

}
