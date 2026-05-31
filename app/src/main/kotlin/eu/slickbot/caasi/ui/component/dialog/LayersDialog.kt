package eu.slickbot.caasi.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.slickbot.caasi.data.api.model.Layer

@Composable
fun LayersDialog(
  layers: List<Layer>,
  selectedLayers: List<Layer>,
  onLayerToggled: (Layer, Boolean) -> Unit,
  onDismissRequest: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text("Layers") },
    text = {
      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(layers) { layer ->
          val checked = layer in selectedLayers
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onLayerToggled(layer, !checked) },
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = layer.title,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.weight(1f),
            )
            Switch(
              checked = checked,
              onCheckedChange = { onLayerToggled(layer, it) },
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text("Done")
      }
    },
  )
}
