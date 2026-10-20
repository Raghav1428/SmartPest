package com.example.smartpest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = LeafGreen, onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EDDF), onPrimaryContainer = Color(0xFF193C29),
    secondary = Color(0xFF53634E), onSecondary = Color.White,
    secondaryContainer = Color(0xFFEBEEE2), onSecondaryContainer = Color(0xFF2B3928),
    tertiary = TomatoRed, onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4DB), onTertiaryContainer = Color(0xFF642719),
    background = GardenPaper, onBackground = GardenInk,
    surface = GardenPaper, onSurface = GardenInk,
    surfaceVariant = Color(0xFFEAEDE5), onSurfaceVariant = Color(0xFF535F54),
    outline = Color(0xFF748171), outlineVariant = Color(0xFFD6DDCF),
    error = Color(0xFFAC352B), errorContainer = Color(0xFFFFE3DC), onErrorContainer = Color(0xFF641C16)
)

private val DarkColors = darkColorScheme(
    primary = LeafGreenLight, onPrimary = Color(0xFF123721),
    primaryContainer = Color(0xFF2E4A35), onPrimaryContainer = Color(0xFFD8EED8),
    secondary = Color(0xFFC3CEB5), onSecondary = Color(0xFF273622),
    secondaryContainer = Color(0xFF303C2E), onSecondaryContainer = Color(0xFFE1E8D8),
    tertiary = Color(0xFFFFB5A1), onTertiary = Color(0xFF5D2116),
    tertiaryContainer = Color(0xFF713628), onTertiaryContainer = Color(0xFFFFDAD0),
    background = Color(0xFF141C17), onBackground = Color(0xFFE1E8DD),
    surface = Color(0xFF141C17), onSurface = Color(0xFFE1E8DD),
    surfaceVariant = Color(0xFF263129), onSurfaceVariant = Color(0xFFBEC9BB),
    outline = Color(0xFF899785), outlineVariant = Color(0xFF3D4B3E),
    error = Color(0xFFFFB4A7), errorContainer = Color(0xFF71302A), onErrorContainer = Color(0xFFFFDAD3)
)

@Composable
fun SmartPestTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(10.dp), medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(24.dp)),
        content = { Surface(color = MaterialTheme.colorScheme.background, content = content) }
    )
}
