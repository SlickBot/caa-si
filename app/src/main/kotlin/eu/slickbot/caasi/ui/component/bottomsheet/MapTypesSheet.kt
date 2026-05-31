package eu.slickbot.caasi.ui.component.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.slickbot.caasi.data.prefs.MapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTypesSheet(
  show: Boolean,
  sheetState: SheetState,
  mapThemes: List<MapTheme>,
  selectedMapTheme: MapTheme,
  onMapThemeSelected: (MapTheme) -> Unit,
  onDismissRequest: () -> Unit,
) {
  if (!show) return
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    dragHandle = null,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      SimpleDragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
      Text(
        text = "Select Map Theme",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
      )
      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        items(mapThemes) { theme ->
          val isSelected = theme == selectedMapTheme
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onMapThemeSelected(theme) }
          ) {
            RadioButton(
              selected = isSelected,
              onClick = { onMapThemeSelected(theme) }
            )
            Spacer(Modifier.width(8.dp))
            Text(
              text = theme.label(),
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }
      }
    }
  }
}

private fun MapTheme.label(): String {
  return when (this) {
    MapTheme.SYSTEM -> "System"
    MapTheme.LIGHT -> "Light"
    MapTheme.DARK -> "Dark"
    MapTheme.SATELLITE -> "Satellite"
  }
}
