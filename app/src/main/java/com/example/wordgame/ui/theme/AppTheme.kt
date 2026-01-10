package com.example.wordgame.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FilterBAndW
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wordgame.R

enum class ThemeId(val title: String) {
    Testflight("Classic (Glassmorphism)"),
    Retro("Retro"),
    Lite("Lite"),
    Sunset("Sunrise/Sunset"),
    Space("Space"),
    SciFi("Sci-fi"),
    Sketchbook("Sketchbook"),
    Monochrome("Monochrome"),
    Eco("Eco"),
    Pixel8("Pixel8"),
    Marvel("Marvel")
}

enum class ThemeStyle {
    Glass,
    Retro,
    Image
}

data class AppTheme(
    val id: ThemeId,
    val style: ThemeStyle,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme,
    @DrawableRes val lightBackground: Int,
    @DrawableRes val darkBackground: Int,
    val overlayLight: Color,
    val overlayDark: Color,
    val scanlines: Boolean,
    val icon: ImageVector
)

object ThemeCatalog {
    val themes: List<AppTheme> = listOf(
        AppTheme(
            id = ThemeId.Testflight,
            style = ThemeStyle.Glass,
            lightScheme = lightColorScheme(
                primary = Color(0xFF0EA5E9),
                onPrimary = Color.White,
                secondary = Color(0xFF14B8A6),
                tertiary = Color(0xFFF97316),
                background = Color(0xFFF6FAFD),
                surface = Color.White,
                surfaceVariant = Color(0xFFEAF2F8),
                onSurface = Color(0xFF0B1A2A),
                onBackground = Color(0xFF0B1A2A)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF0EA5E9),
                onPrimary = Color(0xFFF5FBFF),
                secondary = Color(0xFF14B8A6),
                tertiary = Color(0xFFF97316),
                background = Color(0xFF0B1A2A),
                surface = Color(0xFF122538),
                surfaceVariant = Color(0xFF1C2D3F),
                onSurface = Color(0xFFEAF2F9),
                onBackground = Color(0xFFEAF2F9)
            ),
            lightBackground = R.drawable.bg_classic_light,
            darkBackground = R.drawable.bg_classic_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.12f),
            scanlines = false,
            icon = Icons.Outlined.Star
        ),
        AppTheme(
            id = ThemeId.Retro,
            style = ThemeStyle.Retro,
            lightScheme = lightColorScheme(
                primary = Color(0xFFB78731),
                onPrimary = Color(0xFF1B1209),
                secondary = Color(0xFF9C5526),
                tertiary = Color(0xFFB16630),
                background = Color(0xFFF0E6D2),
                surface = Color(0xFFF8F1E3),
                surfaceVariant = Color(0xFFE5D7BD),
                onSurface = Color(0xFF2B1E14),
                onBackground = Color(0xFF2B1E14)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFFA9782F),
                onPrimary = Color(0xFF1A0F08),
                secondary = Color(0xFF9E742C),
                tertiary = Color(0xFFAB5F31),
                background = Color(0xFF1F1812),
                surface = Color(0xFF2A2016),
                surfaceVariant = Color(0xFF352717),
                onSurface = Color(0xFFF4EDE1),
                onBackground = Color(0xFFF4EDE1)
            ),
            lightBackground = R.drawable.bg_retro_light,
            darkBackground = R.drawable.bg_retro_dark,
            overlayLight = Color.White.copy(alpha = 0.15f),
            overlayDark = Color.Black.copy(alpha = 0.45f),
            scanlines = true,
            icon = Icons.Outlined.Palette
        ),
        AppTheme(
            id = ThemeId.Lite,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF4A6FA5),
                onPrimary = Color.White,
                secondary = Color(0xFF6C7A89),
                tertiary = Color(0xFFA3B5C8),
                background = Color(0xFFF7F9FC),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE5EAF2),
                onSurface = Color(0xFF1B2430),
                onBackground = Color(0xFF1B2430)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF7FA6F3),
                onPrimary = Color(0xFF0E141C),
                secondary = Color(0xFF9AA7B6),
                tertiary = Color(0xFFB8C4D0),
                background = Color(0xFF10151C),
                surface = Color(0xFF171E27),
                surfaceVariant = Color(0xFF1E2732),
                onSurface = Color(0xFFE6EDF5),
                onBackground = Color(0xFFE6EDF5)
            ),
            lightBackground = R.drawable.bg_lite_light,
            darkBackground = R.drawable.bg_lite_dark,
            overlayLight = Color.White.copy(alpha = 0.04f),
            overlayDark = Color.Black.copy(alpha = 0.08f),
            scanlines = false,
            icon = Icons.Outlined.LightMode
        ),
        AppTheme(
            id = ThemeId.Sunset,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFFF9AE48),
                onPrimary = Color(0xFF2A1505),
                secondary = Color(0xFFFDB94C),
                tertiary = Color(0xFFFD7A3D),
                background = Color(0xFFF6F0E0),
                surface = Color(0xFFFFF7E8),
                surfaceVariant = Color(0xFFE8DCC6),
                onSurface = Color(0xFF3B2A1E),
                onBackground = Color(0xFF3B2A1E)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFFD94B21),
                onPrimary = Color(0xFF2A120A),
                secondary = Color(0xFFD04822),
                tertiary = Color(0xFFFDB94C),
                background = Color(0xFF1A1210),
                surface = Color(0xFF241916),
                surfaceVariant = Color(0xFF36251F),
                onSurface = Color(0xFFFCEAD9),
                onBackground = Color(0xFFFCEAD9)
            ),
            lightBackground = R.drawable.bg_sunrise_sunset_light,
            darkBackground = R.drawable.bg_sunrise_sunset_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.35f),
            scanlines = false,
            icon = Icons.Outlined.WbSunny
        ),
        AppTheme(
            id = ThemeId.Space,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF5ABBDD),
                onPrimary = Color(0xFF0C1420),
                secondary = Color(0xFF699BCE),
                tertiary = Color(0xFFE9B974),
                background = Color(0xFFF5F6F3),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE6EBF0),
                onSurface = Color(0xFF1A202A),
                onBackground = Color(0xFF1A202A)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF5ABBDD),
                onPrimary = Color(0xFF0C1420),
                secondary = Color(0xFF699BCE),
                tertiary = Color(0xFFE9B974),
                background = Color(0xFF0B0D10),
                surface = Color(0xFF13161A),
                surfaceVariant = Color(0xFF1A1F25),
                onSurface = Color(0xFFE8EDF2),
                onBackground = Color(0xFFE8EDF2)
            ),
            lightBackground = R.drawable.bg_space_light,
            darkBackground = R.drawable.bg_space_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.35f),
            scanlines = false,
            icon = Icons.Outlined.Public
        ),
        AppTheme(
            id = ThemeId.SciFi,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF455D75),
                onPrimary = Color.White,
                secondary = Color(0xFF6A8894),
                tertiary = Color(0xFF7E9A9B),
                background = Color(0xFFEEF1F3),
                surface = Color(0xFFF7F9FA),
                surfaceVariant = Color(0xFFE0E6EA),
                onSurface = Color(0xFF1E2A36),
                onBackground = Color(0xFF1E2A36)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF1C7189),
                onPrimary = Color(0xFF041316),
                secondary = Color(0xFF217C87),
                tertiary = Color(0xFF1B5364),
                background = Color(0xFF0F1A24),
                surface = Color(0xFF142130),
                surfaceVariant = Color(0xFF1A2B3C),
                onSurface = Color(0xFFE6F0F7),
                onBackground = Color(0xFFE6F0F7)
            ),
            lightBackground = R.drawable.bg_scifi_light,
            darkBackground = R.drawable.bg_scifi_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.4f),
            scanlines = false,
            icon = Icons.Outlined.Bolt
        ),
        AppTheme(
            id = ThemeId.Sketchbook,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF584E46),
                onPrimary = Color(0xFFF9F5EB),
                secondary = Color(0xFFBCAD9B),
                tertiary = Color(0xFF3C362E),
                background = Color(0xFFF4F3EE),
                surface = Color(0xFFFCFBF7),
                surfaceVariant = Color(0xFFE6E3D9),
                onSurface = Color(0xFF2B2620),
                onBackground = Color(0xFF2B2620)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF635E4D),
                onPrimary = Color(0xFF0F0F0F),
                secondary = Color(0xFF4E4E4D),
                tertiary = Color(0xFF282827),
                background = Color(0xFF171717),
                surface = Color(0xFF1D1D1C),
                surfaceVariant = Color(0xFF2A2A28),
                onSurface = Color(0xFFEDEBE6),
                onBackground = Color(0xFFEDEBE6)
            ),
            lightBackground = R.drawable.bg_sketchbook_light,
            darkBackground = R.drawable.bg_sketchbook_dark,
            overlayLight = Color.White.copy(alpha = 0.18f),
            overlayDark = Color.Black.copy(alpha = 0.35f),
            scanlines = false,
            icon = Icons.Outlined.Brush
        ),
        AppTheme(
            id = ThemeId.Monochrome,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF111111),
                onPrimary = Color.White,
                secondary = Color(0xFF4D4D4D),
                tertiary = Color(0xFFA0A0A0),
                background = Color(0xFFFFFFFF),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE6E6E6),
                onSurface = Color(0xFF111111),
                onBackground = Color(0xFF111111)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFFFFFFFF),
                onPrimary = Color(0xFF111111),
                secondary = Color(0xFFB0B0B0),
                tertiary = Color(0xFF7A7A7A),
                background = Color(0xFF000000),
                surface = Color(0xFF0B0B0B),
                surfaceVariant = Color(0xFF1A1A1A),
                onSurface = Color(0xFFF5F5F5),
                onBackground = Color(0xFFF5F5F5)
            ),
            lightBackground = R.drawable.bg_monochrome_light,
            darkBackground = R.drawable.bg_monochrome_dark,
            overlayLight = Color.Transparent,
            overlayDark = Color.Transparent,
            scanlines = false,
            icon = Icons.Outlined.FilterBAndW
        ),
        AppTheme(
            id = ThemeId.Eco,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFF2ECC71),
                onPrimary = Color(0xFF08140B),
                secondary = Color(0xFF1E9E5A),
                tertiary = Color(0xFFA3CB38),
                background = Color(0xFFF2EBD6),
                surface = Color(0xFFF8F4E6),
                surfaceVariant = Color(0xFFE3DAC2),
                onSurface = Color(0xFF2A2418),
                onBackground = Color(0xFF2A2418)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFF2ECC71),
                onPrimary = Color(0xFF07140B),
                secondary = Color(0xFF1E9E5A),
                tertiary = Color(0xFFA3CB38),
                background = Color(0xFF151612),
                surface = Color(0xFF1B1C16),
                surfaceVariant = Color(0xFF2A2A22),
                onSurface = Color(0xFFEAE3D2),
                onBackground = Color(0xFFEAE3D2)
            ),
            lightBackground = R.drawable.bg_eco_light,
            darkBackground = R.drawable.bg_eco_dark,
            overlayLight = Color.White.copy(alpha = 0.14f),
            overlayDark = Color.Black.copy(alpha = 0.4f),
            scanlines = false,
            icon = Icons.Outlined.Eco
        ),
        AppTheme(
            id = ThemeId.Pixel8,
            style = ThemeStyle.Retro,
            lightScheme = lightColorScheme(
                primary = Color(0xFFCB915E),
                onPrimary = Color(0xFF2A170C),
                secondary = Color(0xFFE5B57A),
                tertiary = Color(0xFFBA7B5D),
                background = Color(0xFFF4F0EA),
                surface = Color(0xFFFBF7F1),
                surfaceVariant = Color(0xFFE3DDD2),
                onSurface = Color(0xFF2C2A28),
                onBackground = Color(0xFF2C2A28)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFFA76749),
                onPrimary = Color(0xFF1F120A),
                secondary = Color(0xFFB57756),
                tertiary = Color(0xFF76423A),
                background = Color(0xFF1E1B1A),
                surface = Color(0xFF24201F),
                surfaceVariant = Color(0xFF332D2C),
                onSurface = Color(0xFFEEE7DF),
                onBackground = Color(0xFFEEE7DF)
            ),
            lightBackground = R.drawable.bg_pixel8_light,
            darkBackground = R.drawable.bg_pixel8_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.4f),
            scanlines = true,
            icon = Icons.Outlined.GridOn
        ),
        AppTheme(
            id = ThemeId.Marvel,
            style = ThemeStyle.Image,
            lightScheme = lightColorScheme(
                primary = Color(0xFFED1D24),
                onPrimary = Color.White,
                secondary = Color(0xFFB31217),
                tertiary = Color(0xFFF05A5F),
                background = Color(0xFFF4F0EA),
                surface = Color(0xFFFBF7F1),
                surfaceVariant = Color(0xFFE7DED6),
                onSurface = Color(0xFF1B1A1A),
                onBackground = Color(0xFF1B1A1A)
            ),
            darkScheme = darkColorScheme(
                primary = Color(0xFFED1D24),
                onPrimary = Color.White,
                secondary = Color(0xFFB31217),
                tertiary = Color(0xFFF05A5F),
                background = Color(0xFF1F1918),
                surface = Color(0xFF26201F),
                surfaceVariant = Color(0xFF332B28),
                onSurface = Color(0xFFF4ECE7),
                onBackground = Color(0xFFF4ECE7)
            ),
            lightBackground = R.drawable.bg_marvel_light,
            darkBackground = R.drawable.bg_marvel_dark,
            overlayLight = Color.White.copy(alpha = 0.12f),
            overlayDark = Color.Black.copy(alpha = 0.4f),
            scanlines = false,
            icon = Icons.Outlined.AutoAwesome
        )
    )

    fun themeFor(id: ThemeId): AppTheme = themes.firstOrNull { it.id == id } ?: themes.first()
}

val LocalAppTheme = staticCompositionLocalOf { ThemeCatalog.themes.first() }
