package eu.slickbot.caasi.utils

import android.Manifest
import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissions(): MultiplePermissionsState {
  return rememberMultiplePermissionsState(
    listOf(
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION,
    )
  )
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

@Composable
fun bitmapDescriptor(@DrawableRes resId: Int): BitmapDescriptor {
  val context = LocalContext.current
  return remember(resId) { context.getBitmapDescriptor(resId) }
}
