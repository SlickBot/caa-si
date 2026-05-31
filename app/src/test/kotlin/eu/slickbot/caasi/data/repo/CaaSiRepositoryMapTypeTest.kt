package eu.slickbot.caasi.data.repo

import com.squareup.moshi.Moshi
import eu.slickbot.caasi.data.api.CaaSiApi
import eu.slickbot.caasi.data.db.dao.CacheDao
import eu.slickbot.caasi.data.prefs.MapTheme
import eu.slickbot.caasi.data.prefs.SettingsPrefs
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CaaSiRepositoryMapTypeTest {

  private fun repoWithStoredTheme(stored: String): CaaSiRepository {
    val settingsPrefs = mockk<SettingsPrefs>()
    every { settingsPrefs.mapThemeFlow } returns flowOf(stored)
    return CaaSiRepository(
      api = mockk<CaaSiApi>(),
      cache = mockk<CacheDao>(),
      moshi = Moshi.Builder().build(),
      settingsPrefs = settingsPrefs,
    )
  }

  @Test
  fun getSelectedMapTheme_returnsStoredValue_whenValid() = runBlocking {
    val result = repoWithStoredTheme("DARK").getSelectedMapTheme().first()
    assertEquals(MapTheme.DARK, result)
  }

  @Test
  fun getSelectedMapTheme_fallsBackToSystem_whenStoredValueIsNotRecognised() = runBlocking {
    val result = repoWithStoredTheme("BOGUS_VALUE").getSelectedMapTheme().first()
    assertEquals(MapTheme.SYSTEM, result)
  }
}
