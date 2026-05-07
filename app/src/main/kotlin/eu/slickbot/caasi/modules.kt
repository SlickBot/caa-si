package eu.slickbot.caasi

import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.db.AppDatabase
import eu.slickbot.caasi.data.prefs.SettingsPrefs
import eu.slickbot.caasi.data.repo.CaaSiRepository
import eu.slickbot.caasi.ui.screen.MapViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {
  singleOf(::SettingsPrefs)
  factoryOf(::createHttpClient)
  factoryOf(::CaaSiApi)
  single { AppDatabase.create(androidContext()) }
  single { get<AppDatabase>().cacheDao() }
  factoryOf(::CaaSiRepository)
  viewModelOf(::MapViewModel)
}

/**
 * Helper function
 */
fun createHttpClient(): OkHttpClient {
  return OkHttpClient.Builder().apply {
    connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
    readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
    writeTimeout(API_WRITE_TIMEOUT, TimeUnit.SECONDS)

    addInterceptor { chain ->
      chain.proceed(
        chain.request().newBuilder()
          .header("Accept", "application/json")
          .header("User-Agent", API_USER_AGENT)
          .build()
      )
    }

    if (BuildConfig.DEBUG) {
      addNetworkInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
      })
    }
  }.build()
}
