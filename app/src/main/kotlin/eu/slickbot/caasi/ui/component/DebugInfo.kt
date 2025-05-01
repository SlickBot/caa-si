package eu.slickbot.caasi.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.CameraPositionState

@Composable
fun DebugConsole(
  cameraPositionState: CameraPositionState,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier.fillMaxWidth(),
    text = """
      LAT = ${cameraPositionState.position.target.latitude}
      LNG = ${cameraPositionState.position.target.longitude}
      ZOOM = ${cameraPositionState.position.zoom}
    """.trimIndent(),
  )
}