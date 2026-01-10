package com.example.wordgame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.wordgame.ui.theme.LocalIsDarkTheme
import com.example.wordgame.ui.theme.LocalAppTheme
import com.example.wordgame.ui.theme.ThemeStyle

@Composable
fun LiquidGlassBackground(modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val backgroundRes = if (isDark) appTheme.darkBackground else appTheme.lightBackground
    val overlayColor = if (isDark) appTheme.overlayDark else appTheme.overlayLight
    val backgroundBrush = remember(appTheme.id, isDark) {
        when (appTheme.id) {
            com.example.wordgame.ui.theme.ThemeId.Testflight -> {
                if (isDark) {
                    Brush.linearGradient(listOf(Color(0xFF0B1320), Color(0xFF12283D)))
                } else {
                    Brush.linearGradient(listOf(Color(0xFFEAF7FF), Color(0xFFFEE6D4)))
                }
            }
            else -> null
        }
    }
    val backgroundColor = remember(appTheme.id, isDark) {
        when (appTheme.id) {
            com.example.wordgame.ui.theme.ThemeId.Lite -> if (isDark) {
                Color(0xFF10151C)
            } else {
                Color(0xFFF7F9FC)
            }
            com.example.wordgame.ui.theme.ThemeId.Monochrome -> if (isDark) {
                Color(0xFF000000)
            } else {
                Color(0xFFFFFFFF)
            }
            else -> null
        }
    }
    val glassBlobColors = remember(isDark) {
        if (isDark) {
            listOf(
                Color(0xFF1D4ED8).copy(alpha = 0.35f),
                Color(0xFF0EA5E9).copy(alpha = 0.3f),
                Color(0xFF14B8A6).copy(alpha = 0.25f)
            )
        } else {
            listOf(
                Color(0xFF93C5FD).copy(alpha = 0.4f),
                Color(0xFF67E8F9).copy(alpha = 0.35f),
                Color(0xFFFCD34D).copy(alpha = 0.25f)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when {
            backgroundBrush != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush)
                )
            }
            backgroundColor != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                )
            }
            else -> {
                Image(
                    painter = painterResource(id = backgroundRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
        if (appTheme.style == ThemeStyle.Glass) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val blobRadius = size.minDimension * 0.42f
                val positions = listOf(
                    Offset(size.width * 0.2f, size.height * 0.25f),
                    Offset(size.width * 0.85f, size.height * 0.2f),
                    Offset(size.width * 0.8f, size.height * 0.8f),
                    Offset(size.width * 0.1f, size.height * 0.75f)
                )
                positions.forEachIndexed { index, center ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glassBlobColors[index % glassBlobColors.size],
                                Color.Transparent
                            ),
                            center = center,
                            radius = blobRadius
                        ),
                        radius = blobRadius,
                        center = center
                    )
                }
            }
        }
        if (appTheme.scanlines) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scanlineColor = if (isDark) {
                    Color.White.copy(alpha = 0.035f)
                } else {
                    Color.Black.copy(alpha = 0.06f)
                }
                val lineHeight = 2.dp.toPx()
                val gap = 3.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawRect(
                        color = scanlineColor,
                        topLeft = Offset(0f, y),
                        size = Size(size.width, lineHeight)
                    )
                    y += lineHeight + gap
                }
            }
        }
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    shape: Shape? = null,
    overlayColor: Color? = null,
    borderColorOverride: Color? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val resolvedShape = shape ?: RoundedCornerShape(cornerRadius)
    val overlay = overlayColor ?: if (isDark) appTheme.overlayDark else appTheme.overlayLight
    val resolvedBorderColor = borderColorOverride ?: when (appTheme.style) {
        ThemeStyle.Glass -> scheme.primary.copy(alpha = if (isDark) 0.35f else 0.45f)
        ThemeStyle.Retro -> if (isDark) {
            Color(0xFF3BEAFF).copy(alpha = 0.6f)
        } else {
            Color(0xFF1B2A3A).copy(alpha = 0.5f)
        }
        ThemeStyle.Image -> scheme.secondary.copy(alpha = if (isDark) 0.4f else 0.5f)
    }
    val panelBrush = when (appTheme.style) {
        ThemeStyle.Glass -> {
            Brush.verticalGradient(
                colors = listOf(
                    scheme.surface.copy(alpha = if (isDark) 0.6f else 0.75f),
                    scheme.surfaceVariant.copy(alpha = if (isDark) 0.45f else 0.6f)
                )
            )
        }
        ThemeStyle.Retro -> {
            if (isDark) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1824),
                        Color(0xFF101B28)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF6E3),
                        Color(0xFFF2E4CC)
                    )
                )
            }
        }
        ThemeStyle.Image -> {
            Brush.verticalGradient(
                colors = listOf(
                    scheme.surface.copy(alpha = if (isDark) 0.82f else 0.9f),
                    scheme.surfaceVariant.copy(alpha = if (isDark) 0.7f else 0.85f)
                )
            )
        }
    }
    val innerStroke = when (appTheme.style) {
        ThemeStyle.Glass -> if (isDark) {
            Color.White.copy(alpha = 0.12f)
        } else {
            Color.White.copy(alpha = 0.7f)
        }
        ThemeStyle.Retro -> if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color.White.copy(alpha = 0.6f)
        }
        ThemeStyle.Image -> if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color.White.copy(alpha = 0.3f)
        }
    }
    Box(
        modifier = modifier
            .clip(resolvedShape)
            .background(panelBrush)
            .then(if (overlayColor != null || appTheme.style != ThemeStyle.Retro) Modifier.background(overlay) else Modifier)
            .border(width = if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp, color = resolvedBorderColor, shape = resolvedShape)
            .drawBehind {
                val inset = 3.dp.toPx()
                val corner = (cornerRadius.toPx() - inset).coerceAtLeast(0f)
                drawRoundRect(
                    color = innerStroke,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    cornerRadius = CornerRadius(corner, corner),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(contentPadding)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            content()
        }
    }
}

@Composable
fun GlassChip(text: String, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(if (appTheme.style == ThemeStyle.Retro) 6.dp else 16.dp)
    val (chipBg, chipBorder) = when (appTheme.style) {
        ThemeStyle.Glass -> {
            val bg = scheme.surface.copy(alpha = if (isDark) 0.55f else 0.7f)
            val border = scheme.primary.copy(alpha = if (isDark) 0.35f else 0.45f)
            bg to border
        }
        ThemeStyle.Retro -> {
            val bg = if (isDark) Color(0xFF101B28) else Color(0xFFFFF0D6)
            val border = if (isDark) {
                Color(0xFF7CFF6B).copy(alpha = 0.6f)
            } else {
                Color(0xFF1B2A3A).copy(alpha = 0.45f)
            }
            bg to border
        }
        ThemeStyle.Image -> {
            val bg = scheme.surfaceVariant.copy(alpha = if (isDark) 0.7f else 0.85f)
            val border = scheme.secondary.copy(alpha = if (isDark) 0.35f else 0.5f)
            bg to border
        }
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(chipBg)
            .border(
                if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp,
                chipBorder,
                shape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(if (appTheme.style == ThemeStyle.Retro) 6.dp else 18.dp)
    val (focusedContainer, unfocusedContainer) = when (appTheme.style) {
        ThemeStyle.Glass -> {
            val focused = scheme.surface.copy(alpha = if (isDark) 0.55f else 0.75f)
            val unfocused = scheme.surfaceVariant.copy(alpha = if (isDark) 0.45f else 0.65f)
            focused to unfocused
        }
        ThemeStyle.Retro -> {
            val focused = if (isDark) Color(0xFF0F1824) else Color(0xFFFFF6E3)
            val unfocused = if (isDark) Color(0xFF0C1420) else Color(0xFFF6E7CC)
            focused to unfocused
        }
        ThemeStyle.Image -> {
            val focused = scheme.surface.copy(alpha = if (isDark) 0.82f else 0.9f)
            val unfocused = scheme.surfaceVariant.copy(alpha = if (isDark) 0.7f else 0.85f)
            focused to unfocused
        }
    }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = focusedContainer,
        unfocusedContainerColor = unfocusedContainer,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = scheme.secondary.copy(alpha = 0.7f),
        unfocusedIndicatorColor = scheme.secondary.copy(alpha = 0.4f),
        cursorColor = MaterialTheme.colorScheme.primary
    )
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        placeholder = { Text(placeholder) },
        colors = colors,
        visualTransformation = visualTransformation,
        shape = shape
    )
}
