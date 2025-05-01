package eu.slickbot.caasi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = mdPrimary,
  onPrimary = mdOnPrimary,
  primaryContainer = Color(0xFF003549),
  onPrimaryContainer = mdPrimaryContainer,

  secondary = mdSecondary,
  onSecondary = mdOnSecondary,
  secondaryContainer = Color(0xFF004B5A),
  onSecondaryContainer = mdSecondaryContainer,

  tertiary = mdTertiary,
  onTertiary = mdOnTertiary,
  tertiaryContainer = Color(0xFF3A1C66),
  onTertiaryContainer = mdTertiaryContainer,

  error = mdError,
  onError = mdOnError,
  errorContainer = Color(0xFF8A1E2B),
  onErrorContainer = mdErrorContainer,

  background = Color(0xFF121212),
  onBackground = Color(0xFFECECEC),

  surface = Color(0xFF1E1E1E),
  onSurface = Color(0xFFECECEC),
  surfaceVariant = Color(0xFF2C2C2E),
  onSurfaceVariant = Color(0xFFCACACE),

  outline = Color(0xFF8F9199),
)

private val LightColorScheme = lightColorScheme(
  primary = mdPrimary,
  onPrimary = mdOnPrimary,
  primaryContainer = mdPrimaryContainer,
  onPrimaryContainer = mdOnPrimaryContainer,

  secondary = mdSecondary,
  onSecondary = mdOnSecondary,
  secondaryContainer = mdSecondaryContainer,
  onSecondaryContainer = mdOnSecondaryContainer,

  tertiary = mdTertiary,
  onTertiary = mdOnTertiary,
  tertiaryContainer = mdTertiaryContainer,
  onTertiaryContainer = mdOnTertiaryContainer,

  error = mdError,
  onError = mdOnError,
  errorContainer = mdErrorContainer,
  onErrorContainer = mdOnErrorContainer,

  background = mdBackground,
  onBackground = mdOnBackground,

  surface = mdSurface,
  onSurface = mdOnSurface,
  surfaceVariant = mdSurfaceVariant,
  onSurfaceVariant = mdOnSurfaceVariant,

  outline = mdOutline,
)

@Composable
fun CaaSiTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
//  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
//    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//      val context = LocalContext.current
//      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}
