package eu.slickbot.caasi.ui.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.slickbot.caasi.data.api.model.MapFeature
import eu.slickbot.caasi.data.api.model.zoneColor
import eu.slickbot.caasi.utils.linkified

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneDetailsSheet(
  zone: MapFeature?,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
) {
  if (zone == null) return
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    dragHandle = null,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp)
        .padding(bottom = 24.dp),
    ) {
      SimpleDragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(14.dp)
            .background(zone.layer.zoneColor(), CircleShape),
        )
        Spacer(Modifier.size(10.dp))
        Text(
          text = zone.layer.title,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      val title = zone.feature.nameSi
        ?: zone.feature.nameEn
        ?: zone.layer.title
      Text(
        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge,
      )

      val rows = listOf(
        "Subject" to zone.feature.nameEn,
        "Omejitve" to zone.feature.restrictionsSi,
        "Restrictions" to zone.feature.restrictionsEn,
        "Izjeme" to zone.feature.openExceptionsSi,
        "Exceptions" to zone.feature.openExceptionsEn,
        "Posebne izjeme" to zone.feature.specificExceptionsSi,
        "Specific exceptions" to zone.feature.specificExceptionsEn,
        "Zakonodaja" to zone.feature.regulationSi,
        "Regulation" to zone.feature.regulationEn,
        "Razlog" to zone.feature.reasonSi,
        "Reason" to zone.feature.reasonEn,
        "Informacije" to zone.feature.infoSi,
        "Information" to zone.feature.infoEn,
        "NOTAM (SI)" to zone.feature.notamSi,
        "NOTAM (EN)" to zone.feature.notamEn,
        "Opomba" to zone.feature.remarkSi,
        "Remark" to zone.feature.remarkEn,
      )
        .mapNotNull { (k, v) -> v?.takeIf { it.isNotBlank() }?.let { k to it } }
        .distinctBy { it.second } // drop duplicates when SI/EN list overlaps in the property mapping

      if (rows.isEmpty()) {
        Text(
          text = "No additional details.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      } else {
        val linkColor = MaterialTheme.colorScheme.primary
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          for ((label, value) in rows) {
            Column {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
              )
              Text(
                text = value.linkified(linkColor),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }
  }
}
