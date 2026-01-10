package com.example.wordgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.wordgame.ui.theme.ThemeCatalog
import com.example.wordgame.ui.theme.ThemeId
import com.example.wordgame.ui.theme.WordGameTheme
import com.example.wordgame.ui.WordGameAppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("wordgame_prefs", MODE_PRIVATE)
        val storedThemeId = prefs.getString("theme_id", ThemeId.Testflight.name) ?: ThemeId.Testflight.name
        val normalizedThemeId = runCatching { ThemeId.valueOf(storedThemeId) }
            .getOrElse { ThemeId.Testflight }
            .name
        if (normalizedThemeId != storedThemeId) {
            prefs.edit().putString("theme_id", normalizedThemeId).apply()
        }
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by rememberSaveable { mutableStateOf(systemDark) }
            var themeId by rememberSaveable { mutableStateOf(normalizedThemeId) }
            val appTheme = androidx.compose.runtime.remember(themeId) {
                val resolvedId = runCatching { ThemeId.valueOf(themeId) }.getOrElse { ThemeId.Testflight }
                ThemeCatalog.themeFor(resolvedId)
            }
            WordGameTheme(darkTheme = darkTheme, appTheme = appTheme) {
                WordGameAppRoot(
                    isDarkTheme = darkTheme,
                    onToggleDarkTheme = { darkTheme = it },
                    appTheme = appTheme,
                    onSelectTheme = {
                        themeId = it.id.name
                        prefs.edit().putString("theme_id", it.id.name).apply()
                    }
                )
            }
        }
    }
}
