package eu.slickbot.caasi

import com.squareup.moshi.Moshi
import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.api.http.OkHttpClientBuilder
import eu.slickbot.caasi.data.db.AppDatabase
import eu.slickbot.caasi.data.prefs.SettingsPrefs
import eu.slickbot.caasi.data.repo.CaaSiRepository
import eu.slickbot.caasi.ui.screen.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
  // Preferences
  single { SettingsPrefs(androidContext()) }

  // Network
  single { Moshi.Builder().build() }
  single { OkHttpClientBuilder.build() }
  factory { CaaSiApi(get(), get()) }

  // Database
  single { AppDatabase.create(androidContext()) }
  single { get<AppDatabase>().cacheDao() }

  // Repository
  factory { CaaSiRepository(get(), get(), get(), get()) }

  // UI
  viewModelOf(::MapViewModel)
}
