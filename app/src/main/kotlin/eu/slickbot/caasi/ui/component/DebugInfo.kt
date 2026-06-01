package eu.slickbot.caasi.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.maplibre.compose.camera.CameraState

@Composable
fun DebugConsole(
  cameraState: CameraState,
  modifier: Modifier = Modifier,
) {
  val position = cameraState.position
  Text(
    modifier = modifier.fillMaxWidth(),
    text = """
      LAT = ${position.target.latitude}
      LNG = ${position.target.longitude}
      ZOOM = ${position.zoom}
    """.trimIndent(),
  )
}