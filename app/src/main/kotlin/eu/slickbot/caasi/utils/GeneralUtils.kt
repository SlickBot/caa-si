package eu.slickbot.caasi.utils

import android.content.Context
import android.graphics.Canvas
import android.location.Location
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import eu.slickbot.caasi.data.api.http.HttpException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.net.ssl.SSLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun <T> takeIf(condition: Boolean, function: () -> T?): T? {
  return if (condition) function() else null
}

suspend fun <T, R> Iterable<T>.asyncMap(transform: suspend (T) -> R): List<R> {
  return supervisorScope {
    map { async { transform(it) } }.awaitAll()
  }
}

suspend fun <T, R> Iterable<T>.asyncFlatMap(transform: suspend (T) -> Iterable<R>): List<R> {
  return supervisorScope {
    map { async { transform(it) } }.awaitAll().flatten()
  }
}

@OptIn(ExperimentalContracts::class)
inline fun <R, T> Result<T>.foldSafe(
  onSuccess: (value: T) -> R,
  onFailure: (exception: Throwable) -> R,
): R {
  contract {
    callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
  }
  return fold(
    onSuccess = { value ->
      onSuccess(value)
    },
    onFailure = { exception ->
      if (exception is CancellationException) {
        throw exception
      } else {
        onFailure(exception)
      }
    },
  )
}

fun Throwable.toUserMessage(action: String): String = when (this) {
  is UnknownHostException -> "$action: no internet connection"
  is SocketTimeoutException -> "$action: request timed out"
  is ConnectException -> "$action: couldn't reach server"
  is SSLException -> "$action: secure connection failed"
  is HttpException -> "$action: server returned $code"
  is JsonDataException, is JsonEncodingException -> "$action: couldn't parse response"
  is IOException -> "$action: network error (${message ?: javaClass.simpleName})"
  else -> "$action: ${message ?: javaClass.simpleName}"
}

fun Location.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}

fun Context.getBitmapDescriptor(@DrawableRes resId: Int): BitmapDescriptor {
  val vectorDrawable = requireNotNull(ContextCompat.getDrawable(this, resId))
  val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
  val canvas = Canvas(bitmap)
  vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
  vectorDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
