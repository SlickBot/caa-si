package eu.slickbot.caasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import eu.slickbot.caasi.ui.screen.MapScreen
import eu.slickbot.caasi.ui.theme.CaaSiTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CaaSiTheme {
        MapScreen()
      }
    }
  }

}
