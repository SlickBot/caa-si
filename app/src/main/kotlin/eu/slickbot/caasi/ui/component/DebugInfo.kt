package eu.slickbot.caasi.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.ramani.compose.CameraPositionState

@Composable
fun DebugConsole(
  cameraPositionState: CameraPositionState,
  modifier: Modifier = Modifier,
) {
  val position = cameraPositionState.position
  Text(
    modifier = modifier.fillMaxWidth(),
    text = """
      LAT = ${position.target?.latitude}
      LNG = ${position.target?.longitude}
      ZOOM = ${position.zoom}
    """.trimIndent(),
  )
}