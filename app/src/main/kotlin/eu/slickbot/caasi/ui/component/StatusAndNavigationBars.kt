package eu.slickbot.caasi.ui.component

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun StatusAndNavigationBars(darkTheme: Boolean) {
  val window = LocalActivity.current?.window ?: return
  val view = LocalView.current
  LaunchedEffect(darkTheme) {
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
  }
}
