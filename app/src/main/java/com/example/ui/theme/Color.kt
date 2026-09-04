package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val AmberGold = Color(0xFFFFB300)
val AmberGoldDark = Color(0xFFFF8F00)
val FlameOrange = Color(0xFFFF5722)
val NeonCyan = Color(0xFF00E5FF)
val NeonGreen = Color(0xFF00E676)
val DangerRed = Color(0xFFFF3D71)
val PrimeGold = Color(0xFFFFD700)
val PrimePurple = Color(0xFFD500F9)

// Dark Theme Surfaces
val DarkBg = Color(0xFF0A0E17)
val DarkSurface = Color(0xFF131926)
val DarkSurfaceVariant = Color(0xFF1D263B)
val DarkSurfaceElevated = Color(0xFF24304A)
val DarkBorder = Color(0xFF2C3955)
val TextPrimary = Color(0xFFF0F4FC)
val TextSecondary = Color(0xFF9AACCC)
val TextMuted = Color(0xFF627394)

// Light Theme Surfaces
val LightBg = Color(0xFFF4F6FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8EDF7)
val LightSurfaceElevated = Color(0xFFDEE5F3)
val LightBorder = Color(0xFFC7D3E5)
val LightTextPrimary = Color(0xFF0D1527)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF64748B)

data class AppColors(
  val bg: Color,
  val surface: Color,
  val surfaceVariant: Color,
  val surfaceElevated: Color,
  val border: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val primary: Color = AmberGold,
  val secondary: Color = FlameOrange,
  val isDark: Boolean = true
)

val DarkAppColors = AppColors(
  bg = DarkBg,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  surfaceElevated = DarkSurfaceElevated,
  border = DarkBorder,
  textPrimary = TextPrimary,
  textSecondary = TextSecondary,
  textMuted = TextMuted,
  isDark = true
)

val LightAppColors = AppColors(
  bg = LightBg,
  surface = LightSurface,
  surfaceVariant = LightSurfaceVariant,
  surfaceElevated = LightSurfaceElevated,
  border = LightBorder,
  textPrimary = LightTextPrimary,
  textSecondary = LightTextSecondary,
  textMuted = LightTextMuted,
  isDark = false
)
