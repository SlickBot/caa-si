package eu.slickbot.caasi.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

private val LOCATION_PERMISSIONS = listOf(
  Manifest.permission.ACCESS_COARSE_LOCATION,
  Manifest.permission.ACCESS_FINE_LOCATION,
)

class LocationPermissionsState(
  val hasLocationAccess: Boolean,
  val hasPreciseLocation: Boolean,
  val shouldShowRationale: Boolean,
  val launchMultiplePermissionRequest: () -> Unit,
)

@Composable
fun rememberLocationPermissions(): LocationPermissionsState {
  val context = LocalContext.current
  val activity = LocalActivity.current

  var hasAccess by remember { mutableStateOf(context.hasLocationAccess()) }
  var hasPrecise by remember { mutableStateOf(context.hasPreciseLocation()) }
  var shouldShowRationale by remember { mutableStateOf(activity?.shouldShowLocationRationale() ?: false) }

  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) {
    hasAccess = context.hasLocationAccess()
    hasPrecise = context.hasPreciseLocation()
    shouldShowRationale = activity?.shouldShowLocationRationale() ?: false
  }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        hasAccess = context.hasLocationAccess()
        hasPrecise = context.hasPreciseLocation()
        shouldShowRationale = activity?.shouldShowLocationRationale() ?: false
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  return remember(hasAccess, hasPrecise, shouldShowRationale) {
    LocationPermissionsState(
      hasLocationAccess = hasAccess,
      hasPreciseLocation = hasPrecise,
      shouldShowRationale = shouldShowRationale,
      launchMultiplePermissionRequest = {
        launcher.launch(LOCATION_PERMISSIONS.toTypedArray())
      },
    )
  }
}

private fun Context.hasLocationAccess(): Boolean {
  return LOCATION_PERMISSIONS.any { permission ->
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
  }
}

private fun Context.hasPreciseLocation(): Boolean {
  return ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION,
  ) == PackageManager.PERMISSION_GRANTED
}

private fun Activity.shouldShowLocationRationale(): Boolean {
  return LOCATION_PERMISSIONS.any { permission ->
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
  }
}
