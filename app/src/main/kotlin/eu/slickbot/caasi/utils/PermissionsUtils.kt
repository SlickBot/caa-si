package eu.slickbot.caasi.utils

import android.content.Context
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
  val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000).apply {
    setMinUpdateDistanceMeters(2f)
    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
    setWaitForAccurateLocation(true)
  }.build()
  val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
  val client = LocationServices.getSettingsClient(this)
  val task = client.checkLocationSettings(builder.build())

  task.addOnSuccessListener {
    @Suppress("MissingPermission")
    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      locationCallback,
      mainLooper,
    )
  }

  task.addOnFailureListener { exception ->
    if (exception is ResolvableApiException) {
      // Ignore the error.
    } else {
//      e(exception, "FAILED startLocationRequest")
      exception.printStackTrace()
    }
  }
}


