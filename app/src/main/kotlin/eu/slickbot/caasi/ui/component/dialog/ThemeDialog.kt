package eu.slickbot.caasi.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.slickbot.caasi.data.prefs.MapTheme

@Composable
fun ThemeDialog(
  mapThemes: List<MapTheme>,
  selectedMapTheme: MapTheme,
  onMapThemeSelected: (MapTheme) -> Unit,
  onDismissRequest: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text("Map theme") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .selectableGroup(),
      ) {
        mapThemes.forEach { theme ->
          val selected = theme == selectedMapTheme
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onMapThemeSelected(theme)
                onDismissRequest()
              },
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(
              selected = selected,
              onClick = {
                onMapThemeSelected(theme)
                onDismissRequest()
              },
            )
            Spacer(Modifier.width(8.dp))
            Text(
              text = theme.label(),
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text("Close")
      }
    },
  )
}

private fun MapTheme.label(): String {
  return when (this) {
    MapTheme.SYSTEM -> "System"
    MapTheme.LIGHT -> "Light"
    MapTheme.DARK -> "Dark"
    MapTheme.SATELLITE -> "Satellite"
  }
}
