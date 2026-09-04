package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val FFDarkColorScheme =
  darkColorScheme(
    primary = AmberGold,
    onPrimary = Color(0xFF1E1200),
    primaryContainer = Color(0xFF3F2B00),
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = FlameOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4E1A00),
    onSecondaryContainer = Color(0xFFFFCCBC),
    tertiary = NeonCyan,
    onTertiary = Color(0xFF002026),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = DangerRed,
    onError = Color.White
  )

private val FFLightColorScheme =
  lightColorScheme(
    primary = AmberGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3CD),
    onPrimaryContainer = Color(0xFF5C3B00),
    secondary = FlameOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0D6),
    onSecondaryContainer = Color(0xFF5A1A00),
    tertiary = Color(0xFF0097A7),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = DangerRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) DarkAppColors else LightAppColors
  val colorScheme = if (darkTheme) FFDarkColorScheme else FFLightColorScheme

  CompositionLocalProvider(LocalAppColors provides colors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
