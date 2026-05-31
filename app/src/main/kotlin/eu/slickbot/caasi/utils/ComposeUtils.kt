package eu.slickbot.caasi.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import eu.slickbot.caasi.data.prefs.MapTheme
import eu.slickbot.caasi.data.repo.CaaSiRepository
import org.koin.compose.koinInject

@Composable
fun isAppInDarkTheme(
  repo: CaaSiRepository = koinInject(),
  darkTheme: Boolean = isSystemInDarkTheme(),
): Boolean {
  val theme by repo.getSelectedMapTheme().collectAsStateWithLifecycle(MapTheme.SYSTEM)
  return remember(theme, darkTheme) {
    when (theme) {
      MapTheme.SYSTEM -> darkTheme
      MapTheme.LIGHT -> false
      MapTheme.DARK -> true
      MapTheme.SATELLITE -> true
    }
  }
}

@Composable
fun rememberLocationCallback(callback: (LocationResult) -> Unit): LocationCallback {
  return remember {
    object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        callback(locationResult)
      }
    }
  }
}

@Composable
fun rememberFusedLocationProviderClient(): FusedLocationProviderClient {
  val context = LocalContext.current
  return remember { LocationServices.getFusedLocationProviderClient(context) }
}
