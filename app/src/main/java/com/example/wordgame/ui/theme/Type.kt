package com.example.wordgame.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.wordgame.R

private val glassDisplayFont = FontFamily(
    Font(
        resId = R.font.space_grotesk,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.space_grotesk,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)

private val glassBodyFont = FontFamily(
    Font(
        resId = R.font.manrope,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.manrope,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    )
)

private val retroDisplayFont = FontFamily(
    Font(
        resId = R.font.press_start_2p,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.press_start_2p,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)

private val retroBodyFont = FontFamily(
    Font(
        resId = R.font.vt323,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.vt323,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    )
)

val GlassTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = glassDisplayFont,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.2.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = glassDisplayFont,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp
    ),
    titleLarge = TextStyle(
        fontFamily = glassDisplayFont,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = glassDisplayFont,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = glassBodyFont,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = glassBodyFont,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = glassBodyFont,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontFamily = glassBodyFont,
        fontWeight = FontWeight.Medium
    )
)

val ImageTypography = GlassTypography

val RetroTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = retroDisplayFont,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = retroDisplayFont,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.6.sp
    ),
    titleLarge = TextStyle(
        fontFamily = retroDisplayFont,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontFamily = retroDisplayFont,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.4.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = retroBodyFont,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = retroBodyFont,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = retroBodyFont,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = retroBodyFont,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp
    )
)
