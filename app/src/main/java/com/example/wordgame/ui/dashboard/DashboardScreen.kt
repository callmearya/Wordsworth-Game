package com.example.wordgame.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.wordgame.data.WordInsight
import com.example.wordgame.data.WordLengthOption
import com.example.wordgame.data.WordMeaning
import com.example.wordgame.logic.GameRules
import com.example.wordgame.stats.GameRecord
import com.example.wordgame.stats.GameStats
import com.example.wordgame.ui.GameUiState
import com.example.wordgame.ui.WordGamePlayPane
import com.example.wordgame.ui.components.GlassChip
import com.example.wordgame.ui.components.GlassSurface
import com.example.wordgame.ui.components.GlassTextField
import com.example.wordgame.ui.components.LiquidGlassBackground
import com.example.wordgame.ui.theme.LocalIsDarkTheme
import com.example.wordgame.ui.theme.GreenCorrect
import com.example.wordgame.ui.theme.ThemeCatalog
import com.example.wordgame.ui.theme.ThemeStyle
import com.example.wordgame.ui.theme.AppTheme
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch

private val NavBarHeight = 72.dp
private val NavBubbleSize = 66.dp
private val NavBubbleRise = NavBubbleSize / 2 + 6.dp
private val NavContainerVerticalPadding = 10.dp
private val NavTotalHeight = NavBarHeight + NavBubbleRise + NavContainerVerticalPadding * 2
private val PlayPaneCornerRadius = 12.dp
private val PlayPaneNotchRadius = NavBubbleSize / 2 + 4.dp
private val PlayPaneNotchDepth = PlayPaneNotchRadius - 4.dp

private class NotchedRoundedShape(
    private val cornerRadius: Dp,
    private val notchRadius: Dp,
    private val notchDepth: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerPx = with(density) { cornerRadius.toPx() }
        val notchPx = with(density) { notchRadius.toPx() }
        val depthPx = with(density) { notchDepth.toPx() }
        val maxNotch = max(0f, min(size.width / 2f - cornerPx, size.height - cornerPx))
        val effectiveNotch = min(notchPx, maxNotch)
        val effectiveDepth = min(depthPx, effectiveNotch)
        val roundRect = RoundRect(
            rect = Rect(0f, 0f, size.width, size.height),
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )
        if (effectiveNotch <= 0f) {
            return Outline.Rounded(roundRect)
        }
        val rectPath = Path().apply { addRoundRect(roundRect) }
        val notchCenter = Offset(
            size.width / 2f,
            size.height + (effectiveNotch - effectiveDepth)
        )
        val notchRect = Rect(
            left = notchCenter.x - effectiveNotch,
            top = notchCenter.y - effectiveNotch,
            right = notchCenter.x + effectiveNotch,
            bottom = notchCenter.y + effectiveNotch
        )
        val notchPath = Path().apply { addOval(notchRect) }
        val combined = Path().apply { op(rectPath, notchPath, PathOperation.Difference) }
        return Outline.Generic(combined)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardPagerContent(
    navItems: List<NavItem>,
    pagerState: PagerState,
    dashboardState: DashboardUiState,
    gameState: GameUiState,
    onDictionaryQueryChange: (String) -> Unit,
    onDictionarySearch: () -> Unit,
    onThesaurusQueryChange: (String) -> Unit,
    onThesaurusSearch: () -> Unit,
    onRefreshDailyWords: () -> Unit,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onNewGame: () -> Unit,
    onRefreshStats: () -> Unit,
    onOpenSettings: () -> Unit,
    defaultBottomPadding: Dp,
    playBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        userScrollEnabled = true
    ) { page ->
        val tab = navItems[page].tab
        val bottomPadding = if (tab == DashboardTab.Play) {
            playBottomPadding
        } else {
            defaultBottomPadding
        }
        DashboardPageContent(
            tab = tab,
            dashboardState = dashboardState,
            gameState = gameState,
            onDictionaryQueryChange = onDictionaryQueryChange,
            onDictionarySearch = onDictionarySearch,
            onThesaurusQueryChange = onThesaurusQueryChange,
            onThesaurusSearch = onThesaurusSearch,
            onRefreshDailyWords = onRefreshDailyWords,
            onLetter = onLetter,
            onDelete = onDelete,
            onSubmit = onSubmit,
            onModeSelect = onModeSelect,
            onNewGame = onNewGame,
            onRefreshStats = onRefreshStats,
            onOpenSettings = onOpenSettings,
            bottomPadding = bottomPadding
        )
    }
}

@Composable
private fun DashboardPageContent(
    tab: DashboardTab,
    dashboardState: DashboardUiState,
    gameState: GameUiState,
    onDictionaryQueryChange: (String) -> Unit,
    onDictionarySearch: () -> Unit,
    onThesaurusQueryChange: (String) -> Unit,
    onThesaurusSearch: () -> Unit,
    onRefreshDailyWords: () -> Unit,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onNewGame: () -> Unit,
    onRefreshStats: () -> Unit,
    onOpenSettings: () -> Unit,
    bottomPadding: Dp
) {
    val baseModifier = Modifier
        .fillMaxSize()
        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = bottomPadding)

    if (tab == DashboardTab.Play) {
        Box(modifier = baseModifier) {
            PlayPane(
                state = gameState,
                onLetter = onLetter,
                onDelete = onDelete,
                onSubmit = onSubmit,
                onModeSelect = onModeSelect,
                onNewGame = onNewGame,
                onOpenSettings = onOpenSettings,
                modifier = Modifier.fillMaxSize(),
                fillHeight = true
            )
        }
        return
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = baseModifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (tab) {
            DashboardTab.Dictionary -> DictionaryPane(
                state = dashboardState,
                onQueryChange = onDictionaryQueryChange,
                onSearch = onDictionarySearch
            )
            DashboardTab.DailyWords -> DailyWordsPane(
                state = dashboardState,
                onRefresh = onRefreshDailyWords
            )
            DashboardTab.Play -> PlayPane(
                state = gameState,
                onLetter = onLetter,
                onDelete = onDelete,
                onSubmit = onSubmit,
                onModeSelect = onModeSelect,
                onNewGame = onNewGame,
                onOpenSettings = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
                fillHeight = false
            )
            DashboardTab.Thesaurus -> ThesaurusPane(
                state = dashboardState,
                onQueryChange = onThesaurusQueryChange,
                onSearch = onThesaurusSearch
            )
            DashboardTab.Stats -> StatsPane(
                state = gameState,
                onRefreshStats = onRefreshStats
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardBottomNavBar(
    items: List<NavItem>,
    pagerState: PagerState,
    onSelect: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = com.example.wordgame.ui.theme.LocalAppTheme.current
    val pageOffset = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
        .coerceIn(0f, (items.size - 1).toFloat())
    val progressWithinPage = abs(pageOffset - pageOffset.toInt())
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val itemWidth = maxWidth / items.size
        val primaryIndex = items.indexOfFirst { it.isPrimary }.coerceAtLeast(0)
        val playItem = items[primaryIndex]
        val playInteraction = remember { MutableInteractionSource() }
        val scheme = MaterialTheme.colorScheme
        val barHeight = NavBarHeight
        val bubbleSize = NavBubbleSize
        val bubbleRise = NavBubbleRise
        val totalHeight = barHeight + bubbleRise
        val indicatorColors = when (appTheme.style) {
            ThemeStyle.Glass -> listOf(
                scheme.primary.copy(alpha = if (isDark) 0.7f else 0.9f),
                scheme.secondary.copy(alpha = if (isDark) 0.6f else 0.85f)
            )
            ThemeStyle.Retro -> listOf(
                scheme.secondary.copy(alpha = if (isDark) 0.85f else 0.9f),
                scheme.primary.copy(alpha = if (isDark) 0.7f else 0.8f)
            )
            ThemeStyle.Image -> listOf(
                scheme.primary.copy(alpha = if (isDark) 0.8f else 0.9f),
                scheme.tertiary.copy(alpha = if (isDark) 0.7f else 0.85f)
            )
        }
        val borderColor = when (appTheme.style) {
            ThemeStyle.Glass -> scheme.primary.copy(alpha = if (isDark) 0.35f else 0.45f)
            ThemeStyle.Retro -> scheme.secondary.copy(alpha = if (isDark) 0.65f else 0.5f)
            ThemeStyle.Image -> scheme.onSurface.copy(alpha = if (isDark) 0.25f else 0.3f)
        }
        val barBrush = when (appTheme.style) {
            ThemeStyle.Glass -> Brush.verticalGradient(
                colors = listOf(
                    scheme.surface.copy(alpha = if (isDark) 0.5f else 0.7f),
                    scheme.surfaceVariant.copy(alpha = if (isDark) 0.45f else 0.65f)
                )
            )
            ThemeStyle.Retro -> Brush.verticalGradient(
                colors = listOf(
                    scheme.surfaceVariant.copy(alpha = if (isDark) 0.9f else 0.95f),
                    scheme.surface.copy(alpha = if (isDark) 0.9f else 0.98f)
                )
            )
            ThemeStyle.Image -> Brush.verticalGradient(
                colors = listOf(
                    scheme.surfaceVariant.copy(alpha = if (isDark) 0.85f else 0.92f),
                    scheme.surface.copy(alpha = if (isDark) 0.8f else 0.9f)
                )
            )
        }
        val cornerRadius = if (appTheme.style == ThemeStyle.Retro) 12.dp else 20.dp
        val borderWidth = if (appTheme.style == ThemeStyle.Retro) 2.dp else 1.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight)
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawBehind {
                    val barTop = bubbleRise.toPx()
                    val barHeightPx = barHeight.toPx()
                    val radius = cornerRadius.toPx()
                    val notchRadius = bubbleSize.toPx() / 2f + 8.dp.toPx()
                    val barRect = Rect(0f, barTop, size.width, barTop + barHeightPx)
                    val barPath = Path().apply {
                        addRoundRect(RoundRect(barRect, radius, radius))
                    }
                    val notchCenter = Offset(size.width / 2f, barTop)
                    val notchRect = Rect(
                        left = notchCenter.x - notchRadius,
                        top = notchCenter.y - notchRadius,
                        right = notchCenter.x + notchRadius,
                        bottom = notchCenter.y + notchRadius
                    )
                    val notchPath = Path().apply { addOval(notchRect) }
                    val combined = Path().apply { op(barPath, notchPath, PathOperation.Difference) }
                    drawPath(combined, brush = barBrush)
                    drawPath(
                        combined,
                        color = borderColor,
                        style = Stroke(width = borderWidth.toPx())
                    )
                    if (appTheme.style == ThemeStyle.Glass) {
                        val highlight = Color.White.copy(alpha = if (isDark) 0.12f else 0.2f)
                        clipPath(combined) {
                            drawLine(
                                color = highlight,
                                start = Offset(0f, barTop + 2.dp.toPx()),
                                end = Offset(size.width, barTop + 2.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                    if (appTheme.style == ThemeStyle.Retro) {
                        val scanline = if (isDark) {
                            Color.White.copy(alpha = 0.05f)
                        } else {
                            Color.Black.copy(alpha = 0.08f)
                        }
                        clipPath(combined) {
                            val lineHeight = 1.dp.toPx()
                            val gap = 3.dp.toPx()
                            var y = barTop + 2.dp.toPx()
                            val bottom = barTop + barHeightPx
                            while (y < bottom) {
                                drawRect(
                                    color = scanline,
                                    topLeft = Offset(0f, y),
                                    size = Size(size.width, lineHeight)
                                )
                                y += lineHeight + gap
                            }
                        }
                    }
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val distance = abs(pageOffset - index.toFloat())
                val isSelected = distance < 0.5f
                val alpha = 0.6f + (0.4f * (1f - min(1f, distance)))
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.06f else 1f,
                    label = "nav-item-scale"
                )
                val labelColor = when {
                    isSelected && appTheme.style == ThemeStyle.Retro -> scheme.secondary
                    isSelected -> scheme.primary
                    else -> scheme.onSurface.copy(alpha = if (isDark) 0.65f else 0.7f)
                }
                Column(
                    modifier = Modifier
                        .width(itemWidth)
                        .scale(if (item.isPrimary) 1f else scale)
                        .clickable { onSelect(item.tab) }
                        .padding(bottom = 6.dp)
                        .alpha(alpha),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (item.isPrimary) {
                        Spacer(modifier = Modifier.height(38.dp))
                        NavLabel(
                            text = item.label,
                            color = labelColor,
                            isPrimary = true,
                            style = appTheme.style
                        )
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = labelColor,
                            modifier = Modifier.size(20.dp)
                        )
                        NavLabel(
                            text = item.label,
                            color = labelColor,
                            isPrimary = false,
                            style = appTheme.style
                        )
                    }
                }
            }
        }

        val bubbleOffsetX = itemWidth * primaryIndex + (itemWidth - bubbleSize) / 2
        Box(
            modifier = Modifier
                .offset(x = bubbleOffsetX, y = 0.dp)
                .size(bubbleSize)
                .zIndex(2f)
                .scale(1f + 0.05f * progressWithinPage)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(brush = Brush.radialGradient(colors = indicatorColors))
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = playInteraction,
                        indication = rememberRipple(bounded = true, radius = bubbleSize / 2),
                        onClick = { onSelect(playItem.tab) }
                    )
                    .background(
                        brush = when (appTheme.style) {
                            ThemeStyle.Glass -> Brush.linearGradient(
                                colors = listOf(
                                    scheme.primary.copy(alpha = if (isDark) 0.85f else 0.95f),
                                    scheme.secondary.copy(alpha = if (isDark) 0.8f else 0.9f)
                                )
                            )
                            ThemeStyle.Retro -> Brush.linearGradient(
                                colors = listOf(
                                    scheme.primary.copy(alpha = if (isDark) 0.9f else 1f),
                                    scheme.secondary.copy(alpha = if (isDark) 0.9f else 1f)
                                )
                            )
                            ThemeStyle.Image -> Brush.linearGradient(
                                colors = listOf(
                                    scheme.primary.copy(alpha = if (isDark) 0.9f else 1f),
                                    scheme.tertiary.copy(alpha = if (isDark) 0.85f else 0.95f)
                                )
                            )
                        }
                    )
                    .border(
                        if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp,
                        when (appTheme.style) {
                            ThemeStyle.Glass -> Color.White.copy(alpha = 0.4f)
                            ThemeStyle.Retro -> scheme.secondary.copy(alpha = if (isDark) 0.7f else 0.55f)
                            ThemeStyle.Image -> scheme.onSurface.copy(alpha = if (isDark) 0.2f else 0.3f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = playItem.icon,
                    contentDescription = playItem.label,
                    tint = scheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun NavLabel(
    text: String,
    color: Color,
    isPrimary: Boolean,
    style: ThemeStyle,
    modifier: Modifier = Modifier
) {
    val displayText = if (style == ThemeStyle.Retro) {
        text.uppercase(Locale.getDefault())
    } else {
        text
    }
    val fontSize = when (style) {
        ThemeStyle.Retro -> if (isPrimary) 12.sp else 11.sp
        ThemeStyle.Glass -> if (isPrimary) 12.sp else 10.sp
        ThemeStyle.Image -> if (isPrimary) 12.sp else 10.sp
    }
    Text(
        text = displayText,
        style = MaterialTheme.typography.labelMedium.copy(
            fontSize = fontSize,
            lineHeight = fontSize,
            letterSpacing = if (style == ThemeStyle.Retro) 0.sp else 0.1.sp
        ),
        fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
        color = color,
        maxLines = if (style == ThemeStyle.Retro) 2 else 1,
        overflow = if (style == ThemeStyle.Retro) TextOverflow.Clip else TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun SettingsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onOpenThemes: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.widthIn(min = 220.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Dark theme") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme(it) }
                    )
                },
                onClick = { onToggleDarkTheme(!isDarkTheme) }
            )
            DropdownMenuItem(
                text = { Text("Themes") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null
                    )
                },
                onClick = {
                    onDismiss()
                    onOpenThemes()
                }
            )
            DropdownMenuItem(
                text = { Text("Sign out") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null
                    )
                },
                onClick = {
                    onDismiss()
                    onSignOut()
                }
            )
        }
    }
}

@Composable
private fun ThemePickerOverlay(
    currentTheme: AppTheme,
    themes: List<AppTheme>,
    onSelect: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val scrim = if (isDark) Color(0xFF0B111A).copy(alpha = 0.75f) else Color(0xFFE2E8F0).copy(alpha = 0.7f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        GlassSurface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .heightIn(max = 640.dp),
            cornerRadius = 24.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Themes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Pick a look for your dashboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(themes, key = { it.id }) { theme ->
                        ThemePreviewCard(
                            theme = theme,
                            isSelected = theme.id == currentTheme.id,
                            onSelect = {
                                onSelect(theme)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(
    theme: AppTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val bgRes = if (isDark) theme.darkBackground else theme.lightBackground
    val overlay = if (isDark) theme.overlayDark else theme.overlayLight
    val border = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val backgroundBrush = remember(theme.id, isDark) {
        when (theme.id) {
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
    val backgroundColor = remember(theme.id, isDark) {
        when (theme.id) {
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, border, RoundedCornerShape(18.dp))
            .clickable(onClick = onSelect)
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
                    painter = painterResource(id = bgRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlay)
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = theme.icon,
                    contentDescription = theme.id.title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = theme.id.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when (theme.style) {
                        ThemeStyle.Glass -> "Liquid glass"
                        ThemeStyle.Retro -> "Retro pixel"
                        ThemeStyle.Image -> "Scenic"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    dashboardState: DashboardUiState,
    gameState: GameUiState,
    snackbarHostState: SnackbarHostState,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    appTheme: com.example.wordgame.ui.theme.AppTheme,
    onSelectTheme: (com.example.wordgame.ui.theme.AppTheme) -> Unit,
    onSelectTab: (DashboardTab) -> Unit,
    onDictionaryQueryChange: (String) -> Unit,
    onDictionarySearch: () -> Unit,
    onThesaurusQueryChange: (String) -> Unit,
    onThesaurusSearch: () -> Unit,
    onRefreshDailyWords: () -> Unit,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onNewGame: () -> Unit,
    onRefreshStats: () -> Unit
) {
    val navItems = remember {
        listOf(
            NavItem(DashboardTab.Dictionary, "Dictionary", Icons.Outlined.Search),
            NavItem(DashboardTab.DailyWords, "Daily", Icons.Outlined.AutoStories),
            NavItem(DashboardTab.Play, "Play", Icons.Outlined.PlayCircle, isPrimary = true),
            NavItem(DashboardTab.Thesaurus, "Thesaurus", Icons.Outlined.Book),
            NavItem(DashboardTab.Stats, "Stats", Icons.Outlined.Leaderboard)
        )
    }
    val selectedIndex = navItems.indexOfFirst { it.tab == dashboardState.selectedTab }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { navItems.size }
    )
    val scope = rememberCoroutineScope()
    var settingsExpanded by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    val navHeight = NavTotalHeight
    val defaultBottomPadding = navHeight + 12.dp
    val playBottomPadding = (NavBarHeight + NavContainerVerticalPadding).coerceAtLeast(0.dp)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = navItems[page].tab
            if (tab != dashboardState.selectedTab) {
                onSelectTab(tab)
            }
        }
    }
    LaunchedEffect(selectedIndex) {
        if (!pagerState.isScrollInProgress && pagerState.currentPage != selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }

    val onNavSelect: (DashboardTab) -> Unit = { tab ->
        val index = navItems.indexOfFirst { it.tab == tab }
        if (index != -1 && index != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
        }
        if (tab != dashboardState.selectedTab) {
            onSelectTab(tab)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidGlassBackground()
        DashboardPagerContent(
            navItems = navItems,
            pagerState = pagerState,
            gameState = gameState,
            dashboardState = dashboardState,
            onDictionaryQueryChange = onDictionaryQueryChange,
            onDictionarySearch = onDictionarySearch,
            onThesaurusQueryChange = onThesaurusQueryChange,
            onThesaurusSearch = onThesaurusSearch,
            onRefreshDailyWords = onRefreshDailyWords,
            onLetter = onLetter,
            onDelete = onDelete,
            onSubmit = onSubmit,
            onModeSelect = onModeSelect,
            onNewGame = onNewGame,
            onRefreshStats = onRefreshStats,
            onOpenSettings = { settingsExpanded = true },
            defaultBottomPadding = defaultBottomPadding,
            playBottomPadding = playBottomPadding,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        )
        DashboardBottomNavBar(
            items = navItems,
            pagerState = pagerState,
            onSelect = onNavSelect,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = NavContainerVerticalPadding)
                .navigationBarsPadding()
        )
        SettingsMenu(
            expanded = settingsExpanded,
            onDismiss = { settingsExpanded = false },
            isDarkTheme = isDarkTheme,
            onToggleDarkTheme = onToggleDarkTheme,
            onOpenThemes = { showThemePicker = true },
            onSignOut = onSignOut,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 6.dp, end = 16.dp)
        )
        if (showThemePicker) {
            ThemePickerOverlay(
                currentTheme = appTheme,
                themes = ThemeCatalog.themes,
                onSelect = onSelectTheme,
                onDismiss = { showThemePicker = false },
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(4f)
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = defaultBottomPadding)
        )
    }
}

@Composable
private fun DictionaryPane(
    state: DashboardUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaneHeader(title = "Dictionary", icon = Icons.Outlined.Search)
            GlassTextField(
                value = state.dictionaryQuery,
                onValueChange = onQueryChange,
                placeholder = "Search a word",
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onSearch,
                enabled = !state.isDictionaryLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Search")
            }
            when {
                state.isDictionaryLoading -> LoadingRow("Fetching definition…")
                state.dictionaryError != null -> ErrorRow(state.dictionaryError)
                state.dictionaryResult != null -> WordInsightCard(state.dictionaryResult)
                else -> HintRow("Search to see meaning, family, usage, and synonyms.")
            }
        }
    }
}

@Composable
private fun DailyWordsPane(
    state: DashboardUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaneHeader(
                title = "Words of the Day",
                icon = Icons.Outlined.AutoStories,
                action = {
                    IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                }
            )
            when {
                state.isDailyLoading -> LoadingRow("Curating today’s words…")
                state.dailyError != null -> ErrorRow(state.dailyError)
                state.dailyWords.isEmpty() -> HintRow("No words yet. Tap refresh.")
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.dailyWords.forEach { insight ->
                            WordInsightCard(insight = insight, compact = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayPane(
    state: GameUiState,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onNewGame: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    fillHeight: Boolean = false
) {
    val surfaceModifier = if (fillHeight) {
        modifier.fillMaxSize()
    } else {
        modifier.fillMaxWidth()
    }
    val surfaceShape = if (fillHeight) {
        remember { NotchedRoundedShape(PlayPaneCornerRadius, PlayPaneNotchRadius, PlayPaneNotchDepth) }
    } else {
        null
    }
    val contentBottomInset = if (fillHeight) {
        (PlayPaneNotchDepth - 8.dp).coerceAtLeast(0.dp)
    } else {
        0.dp
    }
    GlassSurface(
        modifier = surfaceModifier,
        cornerRadius = PlayPaneCornerRadius,
        shape = surfaceShape
    ) {
        val columnModifier = if (fillHeight) Modifier.fillMaxSize() else Modifier
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayHeader(onOpenSettings = onOpenSettings)
            WordGamePlayPane(
                state = state,
                onLetter = onLetter,
                onDelete = onDelete,
                onSubmit = onSubmit,
                onModeSelect = onModeSelect,
                onNewGame = onNewGame,
                scaleToFit = fillHeight,
                bottomInset = contentBottomInset,
                modifier = if (fillHeight) {
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
        }
    }
}

@Composable
private fun ThesaurusPane(
    state: DashboardUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaneHeader(title = "Thesaurus", icon = Icons.Outlined.Book)
            GlassTextField(
                value = state.thesaurusQuery,
                onValueChange = onQueryChange,
                placeholder = "Explore synonyms",
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onSearch,
                enabled = !state.isThesaurusLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Find synonyms")
            }
            when {
                state.isThesaurusLoading -> LoadingRow("Finding alternatives…")
                state.thesaurusError != null -> ErrorRow(state.thesaurusError)
                state.thesaurusResult != null -> ThesaurusResultCard(state.thesaurusResult)
                else -> HintRow("Search to see a curated synonym list.")
            }
        }
    }
}

@Composable
private fun StatsPane(
    state: GameUiState,
    onRefreshStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaneHeader(
                title = "Statistics",
                icon = Icons.Outlined.Leaderboard,
                action = {
                    TextButton(onClick = onRefreshStats, enabled = !state.statsState.isLoading) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh stats"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Refresh")
                    }
                }
            )
            when {
                state.statsState.isLoading -> LoadingRow("Syncing stats…")
                state.statsState.stats != null -> {
                    StatsOverview(state.statsState.stats)
                    GuessDistribution(state.statsState.stats)
                    RecentGames(state.statsState.stats.recentGames)
                }
                else -> HintRow(state.statsState.error ?: "Play a round to generate stats.")
            }
        }
    }
}

@Composable
private fun PaneHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        if (action != null) {
            action()
        }
    }
}

@Composable
private fun PlayHeader(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Outlined.PlayCircle, contentDescription = null)
            Text(
                text = "Play",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        GlassSurface(
            cornerRadius = 18.dp,
            contentPadding = PaddingValues(6.dp)
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }
}

@Composable
private fun WordInsightCard(
    insight: WordInsight,
    compact: Boolean = false
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val wordLabel = insight.word.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
            }
            Text(
                text = wordLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (insight.meanings.isNotEmpty()) {
                MeaningList(
                    meanings = insight.meanings,
                    maxMeanings = if (compact) 1 else 3,
                    maxDefinitions = if (compact) 1 else 2,
                    showExamples = !compact,
                    showSynonyms = !compact,
                    showAntonyms = false
                )
                val meaningSynonyms = insight.meanings.flatMap { it.synonyms }.distinct()
                if (compact && insight.synonyms.isNotEmpty()) {
                    SynonymFlow(title = "Synonyms", words = insight.synonyms, compact = true)
                } else if (meaningSynonyms.isEmpty() && insight.synonyms.isNotEmpty()) {
                    SynonymFlow(title = "General synonyms", words = insight.synonyms, compact = false)
                }
            } else {
                val partOfSpeech = insight.partOfSpeech?.takeIf { it.isNotBlank() } ?: "Unknown family"
                Text(
                    text = "Family: ${
                        partOfSpeech.replaceFirstChar { ch ->
                            if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                        }
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = insight.definition ?: "Definition not available yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
                insight.example?.takeIf { it.isNotBlank() }?.let { example ->
                    Text(
                        text = "Usage: $example",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (insight.meanings.isEmpty() && insight.synonyms.isNotEmpty()) {
                SynonymFlow(title = "Synonyms", words = insight.synonyms, compact = compact)
            }
        }
    }
}

@Composable
private fun MeaningList(
    meanings: List<WordMeaning>,
    maxMeanings: Int,
    maxDefinitions: Int,
    showExamples: Boolean,
    showSynonyms: Boolean,
    showAntonyms: Boolean,
    synonymLimit: Int = 8,
    antonymLimit: Int = 6
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        meanings.take(maxMeanings).forEachIndexed { index, meaning ->
            val label = meaning.partOfSpeech?.takeIf { it.isNotBlank() }?.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
            } ?: "Meaning ${index + 1}"
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val definitions = meaning.definitions.take(maxDefinitions)
            if (definitions.isNotEmpty()) {
                definitions.forEach { definition ->
                    Text(
                        text = "• $definition",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = "Definition not available yet.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (showExamples) {
                meaning.examples.firstOrNull()?.let { example ->
                    Text(
                        text = "Usage: $example",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            if (showSynonyms) {
                val synonyms = meaning.synonyms.distinct().take(synonymLimit)
                if (synonyms.isNotEmpty()) {
                    SynonymFlow(title = "Synonyms", words = synonyms, compact = false, limit = synonymLimit)
                }
            }
            if (showAntonyms) {
                val antonyms = meaning.antonyms.distinct().take(antonymLimit)
                if (antonyms.isNotEmpty()) {
                    SynonymFlow(title = "Antonyms", words = antonyms, compact = false, limit = antonymLimit)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SynonymFlow(
    title: String,
    words: List<String>,
    compact: Boolean,
    limit: Int? = null
) {
    val maxItems = limit ?: if (compact) 6 else 10
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            words.take(maxItems).forEach { synonym ->
                GlassChip(text = synonym)
            }
        }
    }
}

@Composable
private fun ThesaurusResultCard(result: ThesaurusResult) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = result.word.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (result.meanings.isNotEmpty()) {
                MeaningList(
                    meanings = result.meanings,
                    maxMeanings = 3,
                    maxDefinitions = 2,
                    showExamples = false,
                    showSynonyms = true,
                    showAntonyms = true
                )
                val meaningSynonyms = result.meanings.flatMap { it.synonyms }.distinct()
                val meaningAntonyms = result.meanings.flatMap { it.antonyms }.distinct()
                if (meaningSynonyms.isEmpty() && result.synonyms.isNotEmpty()) {
                    SynonymFlow(title = "General synonyms", words = result.synonyms, compact = false)
                }
                if (meaningAntonyms.isEmpty() && result.antonyms.isNotEmpty()) {
                    SynonymFlow(title = "General antonyms", words = result.antonyms, compact = false)
                }
            } else {
                result.partOfSpeech?.takeIf { it.isNotBlank() }?.let { pos ->
                    Text(
                        text = pos.replaceFirstChar { ch ->
                            if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                result.definition?.takeIf { it.isNotBlank() }?.let { definition ->
                    Text(text = definition, style = MaterialTheme.typography.bodyMedium)
                }
                if (result.synonyms.isNotEmpty()) {
                    SynonymFlow(title = "Synonyms", words = result.synonyms, compact = false)
                }
                if (result.antonyms.isNotEmpty()) {
                    SynonymFlow(title = "Antonyms", words = result.antonyms, compact = false)
                }
            }
        }
    }
}

@Composable
private fun LoadingRow(message: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        Text(text = message, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ErrorRow(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun HintRow(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StatsOverview(stats: GameStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Played",
                value = stats.totalGames.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Win %",
                value = "${(stats.winRate * 100).toInt()}%",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Loss %",
                value = "${(stats.lossRate * 100).toInt()}%",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Streak",
                value = stats.currentStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Best",
                value = stats.bestStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Misses",
                value = stats.losses.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        StatCard(
            title = "Avg guesses",
            value = String.format(Locale.getDefault(), "%.2f", stats.averageGuesses),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier,
        cornerRadius = 18.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GuessDistribution(stats: GameStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Guess Distribution",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        val maxValue = max(
            1,
            max(
                stats.guessDistribution.values.maxOrNull() ?: 0,
                stats.misses
            )
        )
        (1..GameRules.MAX_ATTEMPTS).forEach { attempt ->
            val value = stats.guessDistribution[attempt] ?: 0
            DistributionRow(attempt.toString(), value, maxValue)
        }
        DistributionRow("X", stats.misses, maxValue)
    }
}

@Composable
private fun DistributionRow(label: String, value: Int, maxValue: Int) {
    val isDark = LocalIsDarkTheme.current
    val barBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.55f else 0.7f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )
        val fraction = value.toFloat() / maxValue.toFloat()
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(barBackground),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (value > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenCorrect.copy(alpha = 0.8f))
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                )
            }
            Text(
                text = value.toString(),
                modifier = Modifier.padding(horizontal = 8.dp),
                color = if (value > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecentGames(records: List<GameRecord>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Recent Games",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        if (records.isEmpty()) {
            HintRow("Play a few rounds to populate this list.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                records.forEach { record ->
                    RecentGameRow(record)
                }
            }
        }
    }
}

@Composable
private fun RecentGameRow(record: GameRecord) {
    val statusColor = if (record.won) GreenCorrect else MaterialTheme.colorScheme.error
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.word,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (record.won) {
                        "Won in ${record.guessCount} guess(es)"
                    } else {
                        "Missed in ${GameRules.MAX_ATTEMPTS}"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = if (record.won) "Win" else "Loss",
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class NavItem(
    val tab: DashboardTab,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isPrimary: Boolean = false
)
