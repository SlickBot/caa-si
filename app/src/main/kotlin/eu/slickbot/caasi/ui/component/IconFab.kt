package eu.slickbot.caasi.ui.component

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconFab(
  onClick: () -> Unit,
  imageVector: ImageVector,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
) {
  FloatingActionButton(
    modifier = modifier,
    onClick = onClick,
  ) {
    Icon(imageVector, contentDescription)
  }
}
