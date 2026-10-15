package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val title: String, val subtitle: String) {
  SYSTEM("Sistem", "Mengikuti pengaturan tema perangkat Android"),
  LIGHT("Terang (Light)", "Tampilan cerah, bersih, dan segar"),
  DARK("Gelap (Dark)", "Tampilan malam nyaman di mata & hemat baterai")
}

class ThemePreferences(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("boostly_theme_prefs", Context.MODE_PRIVATE)

  private val _themeMode = MutableStateFlow(loadSavedMode())
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  private fun loadSavedMode(): AppThemeMode {
    val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
    return try {
      AppThemeMode.valueOf(saved)
    } catch (e: Exception) {
      AppThemeMode.LIGHT
    }
  }

  fun setThemeMode(mode: AppThemeMode) {
    prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    _themeMode.value = mode
  }

  fun toggleTheme() {
    val current = _themeMode.value
    val next = if (current == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
    setThemeMode(next)
  }

  companion object {
    private const val KEY_THEME_MODE = "key_app_theme_mode"
  }
}
