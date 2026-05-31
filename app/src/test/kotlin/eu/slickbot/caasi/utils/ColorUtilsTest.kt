package eu.slickbot.caasi.utils

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorUtilsTest {

  @Test
  fun toHexString_formatsOpaqueRgb() {
    assertEquals("#FF0000", Color(0xFFFF0000).toHexString())
    assertEquals("#1A73E8", Color(0xFF1A73E8).toHexString())
  }

  @Test
  fun toHexString_ignoresAlpha() {
    assertEquals("#1A73E8", Color(0x801A73E8).toHexString())
  }
}
