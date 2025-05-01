package eu.slickbot.caasi.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun LinearLoader(show: Boolean, modifier: Modifier = Modifier) {
  val alpha by animateFloatAsState(if (show) 1f else 0f)
  LinearProgressIndicator(modifier.fillMaxWidth().alpha(alpha))
}
