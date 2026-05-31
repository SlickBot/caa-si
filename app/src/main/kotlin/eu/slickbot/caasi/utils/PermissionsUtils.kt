package eu.slickbot.caasi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

fun Context.startLocationRequest(
  fusedLocationClient: FusedLocationProviderClient,
  locationCallback: LocationCallback,
) {
  if (!hasLocationPermission()) return

  val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2 * 1000).apply {
    setMinUpdateDistanceMeters(2f)
    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
    setWaitForAccurateLocation(true)
  }.build()

  val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
  val client = LocationServices.getSettingsClient(this)
  val task = client.checkLocationSettings(builder.build())

  task.addOnSuccessListener {
    try {
      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        mainLooper,
      )
    } catch (e: SecurityException) {
      // Permission was revoked between the check above and this callback.
      e.printStackTrace()
    }
  }

  task.addOnFailureListener { exception ->
    if (exception is ResolvableApiException) {
      // Ignore the error.
    } else {
      exception.printStackTrace()
    }
  }
}

private fun Context.hasLocationPermission(): Boolean {
  val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
  val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
  return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}
