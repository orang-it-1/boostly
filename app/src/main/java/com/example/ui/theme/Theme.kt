package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalThemeIsDark = compositionLocalOf { false }

@Composable
fun isThemeDark(): Boolean = LocalThemeIsDark.current

private val DarkColorScheme = darkColorScheme(
  primary = BoostlyNeonLime,
  onPrimary = Color(0xFF0A1910),
  primaryContainer = BoostlyForestGreen,
  onPrimaryContainer = BoostlyNeonLimeLight,
  secondary = BoostlyEmerald,
  onSecondary = Color.Black,
  tertiary = BoostlyGold,
  onTertiary = Color.Black,
  background = Color(0xFF0A140E),
  onBackground = Color(0xFFF3F4F6),
  surface = Color(0xFF112217),
  onSurface = Color(0xFFF9FAFB),
  surfaceVariant = Color(0xFF183323),
  onSurfaceVariant = Color(0xFFA3B8AB),
  outline = Color(0xFF264A35),
  error = BoostlyAlert,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = BoostlyForestGreen,
  onPrimary = Color.White,
  primaryContainer = BoostlyNeonLimeLight,
  onPrimaryContainer = Color(0xFF0A1910),
  secondary = BoostlyEmerald,
  onSecondary = Color.White,
  tertiary = BoostlyGoldDark,
  onTertiary = Color.White,
  background = BoostlyAppBg,
  onBackground = BoostlyTextDark,
  surface = Color.White,
  onSurface = BoostlyTextDark,
  surfaceVariant = BoostlyMintCard,
  onSurfaceVariant = BoostlyTextGray,
  outline = BoostlyBorderLight,
  error = BoostlyAlert,
  onError = Color.White
)

@Composable
fun SosmedGrowTheme(
  themeMode: AppThemeMode = AppThemeMode.LIGHT,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val systemDark = isSystemInDarkTheme()
  val darkTheme = when (themeMode) {
    AppThemeMode.SYSTEM -> systemDark
    AppThemeMode.DARK -> true
    AppThemeMode.LIGHT -> false
  }

  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  CompositionLocalProvider(
    LocalThemeIsDark provides darkTheme
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
