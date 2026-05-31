package eu.slickbot.caasi.ui.component.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.slickbot.caasi.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
  isDebugVisible: Boolean,
  onToggleDebug: () -> Unit,
  onLayersClick: () -> Unit,
  onThemeClick: () -> Unit,
  onOpenUrl: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  ModalDrawerSheet(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(horizontal = 12.dp, vertical = 28.dp),
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "CAA-SI",
          style = MaterialTheme.typography.titleLarge,
        )
        Text(
          text = "Slovenian airspace map",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Spacer(Modifier.height(24.dp))

      DrawerActionItem(
        icon = Icons.Default.Layers,
        label = "Layers",
        onClick = onLayersClick,
      )
      DrawerActionItem(
        icon = Icons.Default.Map,
        label = "Map theme",
        onClick = onThemeClick,
      )
      DrawerToggleItem(
        icon = Icons.Default.BugReport,
        label = "Show debug",
        checked = isDebugVisible,
        onToggle = onToggleDebug,
      )

      Spacer(Modifier.weight(1f))

      Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          text = "DATA SOURCES",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
        )
        dataSources.forEach { source ->
          AttributionRow(source = source, onClick = { onOpenUrl(source.url) })
        }
      }

      Spacer(Modifier.height(20.dp))

      Text(
        text = "v${BuildConfig.VERSION_NAME}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp),
      )
    }
  }
}

@Composable
private fun DrawerActionItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.width(16.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
    )
  }
}

@Composable
private fun DrawerToggleItem(
  icon: ImageVector,
  label: String,
  checked: Boolean,
  onToggle: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .clickable { onToggle() }
      .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.width(16.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
      modifier = Modifier.weight(1f),
    )
    Switch(
      checked = checked,
      onCheckedChange = { onToggle() },
    )
  }
}

@Composable
private fun AttributionRow(
  source: DataSource,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = source.name,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = source.role,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Spacer(Modifier.width(12.dp))
    Icon(
      imageVector = Icons.AutoMirrored.Filled.OpenInNew,
      contentDescription = "Open ${source.name}",
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(18.dp),
    )
  }
}

private data class DataSource(
  val name: String,
  val role: String,
  val url: String,
)

private val dataSources = listOf(
  DataSource("CAA Slovenia", "Airspace data", "https://www.caa.si"),
  DataSource("OpenFreeMap · OpenStreetMap", "Base map", "https://openfreemap.org"),
  DataSource("Esri · Maxar · Earthstar", "Satellite imagery", "https://www.esri.com"),
  DataSource("MapLibre", "Map rendering", "https://maplibre.org"),
)
