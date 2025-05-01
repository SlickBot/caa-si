package eu.slickbot.caasi.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MapType
import eu.slickbot.caasi.data.repo.CaaSiRepository
import org.koin.compose.koinInject

@Composable
fun isAppInDarkTheme(
  repo: CaaSiRepository = koinInject(),
  darkTheme: Boolean = isSystemInDarkTheme(),
): Boolean {
  val mapType by repo.getSelectedMapType().collectAsStateWithLifecycle(MapType.NORMAL)
  return remember(mapType, darkTheme) {
    when (mapType) {
      MapType.NONE -> false
      MapType.NORMAL -> darkTheme
      MapType.SATELLITE -> true
      MapType.TERRAIN -> false
      MapType.HYBRID -> true
    }
  }
}
