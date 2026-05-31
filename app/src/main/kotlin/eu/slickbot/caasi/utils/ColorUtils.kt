package eu.slickbot.caasi.utils

import androidx.compose.ui.graphics.Color

/** "#RRGGBB" form for ramani-maps string colors. Alpha is conveyed via the opacity param. */
fun Color.toHexString(): String {
  val r = (red * 255f + 0.5f).toInt()
  val g = (green * 255f + 0.5f).toInt()
  val b = (blue * 255f + 0.5f).toInt()
  return String.format("#%02X%02X%02X", r, g, b)
}
