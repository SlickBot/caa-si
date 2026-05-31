package eu.slickbot.caasi.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPrefs(
  private val context: Context,
) {

  private object Keys {
    val LAYERS = stringSetPreferencesKey("layers")
    val MAP_TYPE = stringPreferencesKey("map_type")
  }

  private val Context.dataStore by preferencesDataStore(name = "map_settings")

  val layersFlow: Flow<Set<String>?> =
    context.dataStore.data.map {
      it[Keys.LAYERS]
    }

  val mapThemeFlow: Flow<String> =
    context.dataStore.data.map {
      it[Keys.MAP_TYPE] ?: MapTheme.SYSTEM.name
    }

  suspend fun saveLayers(ids: Set<String>) {
    context.dataStore.edit {
      it[Keys.LAYERS] = ids
    }
  }

  suspend fun saveMapTheme(id: String) {
    context.dataStore.edit {
      it[Keys.MAP_TYPE] = id
    }
  }
}
