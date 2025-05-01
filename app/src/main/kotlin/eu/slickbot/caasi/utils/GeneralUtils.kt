package eu.slickbot.caasi.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun <T> takeIf(condition: Boolean, function: () -> T?): T? {
  return if (condition) function() else null
}

suspend fun <T, R> Iterable<T>.asyncMap(transform: suspend (T) -> R): List<R> {
  return coroutineScope {
    map { async { transform(it) } }.awaitAll()
  }
}

suspend fun <T, R> Iterable<T>.asyncFlatMap(transform: suspend (T) -> Iterable<R>): List<R> {
  return coroutineScope {
    map { async { transform(it) } }.awaitAll().flatten()
  }
}
