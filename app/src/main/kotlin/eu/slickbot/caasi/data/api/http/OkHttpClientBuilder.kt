package eu.slickbot.caasi.data.api.http

import eu.slickbot.caasi.API_CONNECT_TIMEOUT
import eu.slickbot.caasi.API_READ_TIMEOUT
import eu.slickbot.caasi.API_USER_AGENT
import eu.slickbot.caasi.API_WRITE_TIMEOUT
import eu.slickbot.caasi.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object OkHttpClientBuilder {

  fun build(
    connectTimeout: Duration = API_CONNECT_TIMEOUT.seconds,
    readTimeout: Duration = API_READ_TIMEOUT.seconds,
    writeTimeout: Duration = API_WRITE_TIMEOUT.seconds,
    userAgent: String = API_USER_AGENT,
    enableHttpLogging: Boolean = BuildConfig.DEBUG,
  ): OkHttpClient {
    return OkHttpClient.Builder().apply {
      connectTimeout(connectTimeout)
      readTimeout(readTimeout)
      writeTimeout(writeTimeout)

      addInterceptor { chain ->
        chain.proceed(
          chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", userAgent)
            .build()
        )
      }

      if (enableHttpLogging) {
        addNetworkInterceptor(
          HttpLoggingInterceptor().apply { level = Level.BODY }
        )
      }
    }.build()
  }
}
