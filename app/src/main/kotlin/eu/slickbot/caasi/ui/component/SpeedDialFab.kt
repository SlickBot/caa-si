package eu.slickbot.caasi.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class SpeedDialItem(
  val icon: ImageVector,
  val label: String,
  val onClick: () -> Unit,
)

@Composable
fun SpeedDialFab(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  icon: ImageVector,
  items: List<SpeedDialItem>,
  modifier: Modifier = Modifier,
) {
  val rotation by animateFloatAsState(
    targetValue = if (expanded) 45f else 0f,
    animationSpec = tween(durationMillis = 120),
    label = "speedDialRotation",
  )
  val anim = tween<Float>(durationMillis = 120)
  val animSize = tween<androidx.compose.ui.unit.IntSize>(durationMillis = 120)

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    FloatingActionButton(onClick = { onExpandedChange(!expanded) }) {
      Icon(icon, contentDescription = null, modifier = Modifier.rotate(rotation))
    }
    AnimatedVisibility(
      visible = expanded,
      enter = fadeIn(anim) + expandVertically(animSize, expandFrom = Alignment.Top),
      exit = fadeOut(anim) + shrinkVertically(animSize, shrinkTowards = Alignment.Top),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (item in items) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            SmallFloatingActionButton(
              onClick = {
                onExpandedChange(false)
                item.onClick()
              },
            ) {
              Icon(item.icon, contentDescription = item.label)
            }
            Surface(
              modifier = Modifier.padding(start = 12.dp),
              color = MaterialTheme.colorScheme.surface,
              shape = MaterialTheme.shapes.small,
              tonalElevation = 2.dp,
              shadowElevation = 2.dp,
            ) {
              Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }
        }
      }
    }
  }
}
