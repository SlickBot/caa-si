package eu.slickbot.caasi.data.api.model

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

fun Layer.zoneColor(): Color {
  val hue = ((id.hashCode() % 360) + 360) % 360
  val argb = AndroidColor.HSVToColor(floatArrayOf(hue.toFloat(), 0.7f, 0.9f))
  return Color(argb)
}
