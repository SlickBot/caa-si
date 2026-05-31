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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.slickbot.caasi.BuildConfig

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
    Column(modifier = Modifier.fillMaxHeight()) {
      Spacer(Modifier.height(28.dp))
      Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "CAA-SI",
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Slovenian airspace map",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(Modifier.height(20.dp))

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

      DrawerSectionLabel("DATA SOURCES")
      dataSources.forEach { source ->
        DrawerSourceItem(source = source, onClick = { onOpenUrl(source.url) })
      }
      DrawerTextItem("v${BuildConfig.VERSION_NAME}")

      Spacer(Modifier.height(12.dp))
    }
  }
}

@Composable
private fun DrawerSectionLabel(text: String) {
  Text(
    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    text = text,
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.primary,
    letterSpacing = 1.sp,
  )
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
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
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
      .clickable { onToggle() }
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      modifier = Modifier.weight(1f),
      text = label,
      style = MaterialTheme.typography.bodyLarge,
    )
    Switch(
      checked = checked,
      onCheckedChange = { onToggle() },
    )
  }
}

@Composable
private fun DrawerSourceItem(
  source: DataSource,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = source.name,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = source.role,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Icon(
      imageVector = Icons.AutoMirrored.Filled.OpenInNew,
      contentDescription = "Open ${source.name} website",
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(18.dp),
    )
  }
}

@Composable
private fun DrawerTextItem(text: String) {
  Text(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )
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
