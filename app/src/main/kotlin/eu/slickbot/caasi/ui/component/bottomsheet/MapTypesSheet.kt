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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MapType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTypesSheet(
  sheetState: SheetState,
  mapTypes: List<MapType>,
  selectedMapType: MapType,
  onMapTypeSelected: (MapType) -> Unit,
  onDismissRequest: () -> Unit,
) {
  if (sheetState.isVisible) {
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = sheetState,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Select Map Type",
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
        )
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          items(mapTypes) { mapType ->
            val isChecked = mapType == selectedMapType
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onMapTypeSelected(mapType) }
            ) {
              Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                  onMapTypeSelected(mapType)
                }
              )
              Spacer(Modifier.width(8.dp))
              Text(
                text = mapType.toString(),
                style = MaterialTheme.typography.bodyLarge,
              )
            }
          }
        }
      }
    }
  }
}
