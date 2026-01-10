package com.example.wordgame.ui

import android.text.format.DateFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordgame.R
import com.example.wordgame.data.WordDetails
import com.example.wordgame.data.WordLengthOption
import com.example.wordgame.logic.GameRules
import com.example.wordgame.logic.LetterFeedback
import com.example.wordgame.logic.LetterStatus
import com.example.wordgame.stats.GameRecord
import com.example.wordgame.stats.GameStats
import com.example.wordgame.ui.theme.GrayAbsent
import com.example.wordgame.ui.theme.GreenCorrect
import com.example.wordgame.ui.theme.YellowPresent
import com.example.wordgame.ui.auth.AuthScreen
import com.example.wordgame.ui.auth.AuthViewModel
import com.example.wordgame.ui.dashboard.DashboardScreen
import com.example.wordgame.ui.dashboard.DashboardViewModel
import com.example.wordgame.ui.components.GlassSurface
import com.example.wordgame.ui.theme.LocalIsDarkTheme
import com.example.wordgame.ui.theme.LocalAppTheme
import com.example.wordgame.ui.theme.ThemeStyle
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random

private val firstRow = "QWERTYUIOP"
private val secondRow = "ASDFGHJKL"
private val thirdRow = "ZXCVBNM"

@Composable
fun WordGameAppRoot(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    appTheme: com.example.wordgame.ui.theme.AppTheme,
    onSelectTheme: (com.example.wordgame.ui.theme.AppTheme) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    if (authState.user == null) {
        AuthScreen(
            state = authState,
            onGoogleToken = authViewModel::signInWithGoogle,
            onGoogleError = authViewModel::setError
        )
    } else {
        val userKey = authState.user?.uid ?: "signed-in"
        val viewModel: WordGameViewModel = viewModel(
            key = "game-$userKey",
            factory = WordGameViewModel.Factory
        )
        val dashboardViewModel: DashboardViewModel = viewModel(
            key = "dashboard-$userKey",
            factory = DashboardViewModel.Factory
        )
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val dashboardState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHost = remember { SnackbarHostState() }
        val appContext = LocalContext.current.applicationContext
        val soundPlayer = remember(appContext) { SoundEffectPlayer(appContext) }
        DisposableEffect(soundPlayer) {
            onDispose { soundPlayer.release() }
        }
        var showConfetti by remember { mutableStateOf(false) }
        var confettiTrigger by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is GameUiEvent.Snackbar -> snackbarHost.showSnackbar(event.message)
                    is GameUiEvent.RoundComplete -> {
                        if (event.won) {
                            soundPlayer.playSuccess()
                        } else {
                            soundPlayer.playFail()
                        }
                        if (event.won) {
                            confettiTrigger += 1
                            showConfetti = true
                        }
                    }
                }
            }
        }

        val userMessage = uiState.userMessage
        LaunchedEffect(userMessage) {
            if (userMessage != null) {
                snackbarHost.showSnackbar(userMessage)
                viewModel.consumeUserMessage()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                dashboardState = dashboardState,
                gameState = uiState,
                snackbarHostState = snackbarHost,
                onSignOut = authViewModel::signOut,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                appTheme = appTheme,
                onSelectTheme = onSelectTheme,
                onSelectTab = dashboardViewModel::selectTab,
                onDictionaryQueryChange = dashboardViewModel::updateDictionaryQuery,
                onDictionarySearch = dashboardViewModel::searchDictionary,
                onThesaurusQueryChange = dashboardViewModel::updateThesaurusQuery,
                onThesaurusSearch = dashboardViewModel::searchThesaurus,
                onRefreshDailyWords = { dashboardViewModel.refreshDailyWords(force = true) },
                onLetter = viewModel::onLetterInput,
                onDelete = viewModel::onBackspace,
                onSubmit = viewModel::onSubmitGuess,
                onModeSelect = viewModel::onModeSelected,
                onNewGame = viewModel::onNewGameRequested,
                onRefreshStats = { viewModel.refreshStats(force = true) }
            )
            if (showConfetti) {
                ConfettiBurst(
                    trigger = confettiTrigger,
                    onFinished = { showConfetti = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordGameScreen(
    state: GameUiState,
    snackbarHostState: SnackbarHostState,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onToggleStats: (Boolean) -> Unit,
    onNewGame: () -> Unit,
    onRefreshStats: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.showStats) {
        ModalBottomSheet(
            onDismissRequest = { onToggleStats(false) },
            sheetState = sheetState
        ) {
            StatsSheet(
                state = state.statsState,
                wordLength = state.option.letters,
                isCloudEnabled = state.isCloudEnabled,
                onRefresh = onRefreshStats
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WordGame") },
                actions = {
                    IconButton(onClick = { onToggleStats(true) }) {
                        Icon(
                            imageVector = Icons.Outlined.Leaderboard,
                            contentDescription = "Statistics"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
    WordGamePlayPane(
        state = state,
        onLetter = onLetter,
        onDelete = onDelete,
        onSubmit = onSubmit,
        onModeSelect = onModeSelect,
        onNewGame = onNewGame,
        showInlineLoading = false,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )

            if (state.isLoadingWord) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    GlassSurface(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        cornerRadius = 28.dp
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Fetching a new word and its meaning from the open API...",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordGamePlayPane(
    state: GameUiState,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    onModeSelect: (WordLengthOption) -> Unit,
    onNewGame: () -> Unit,
    showInlineLoading: Boolean = true,
    scaleToFit: Boolean = false,
    bottomInset: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val containerModifier = if (scaleToFit) {
        modifier.fillMaxSize()
    } else {
        modifier.fillMaxWidth()
    }
    BoxWithConstraints(modifier = containerModifier) {
        val popupMaxHeight = (maxHeight * 0.7f).coerceIn(240.dp, 440.dp)
        val columnModifier = if (scaleToFit) {
            Modifier.fillMaxSize()
        } else {
            Modifier.fillMaxWidth()
        }
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Choose your word length",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "You have ${GameRules.MAX_ATTEMPTS} attempts to guess the word.",
                    style = MaterialTheme.typography.bodyMedium
                )
                WordLengthSelector(
                    selected = state.option,
                    onSelect = onModeSelect,
                    enabled = !state.isLoadingWord
                )
            }
            if (showInlineLoading && state.isLoadingWord) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(
                        text = "Fetching a new word and meaning…",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().then(
                    if (scaleToFit) {
                        Modifier.weight(1f, fill = true)
                    } else {
                        Modifier
                    }
                ),
                contentAlignment = Alignment.TopCenter
            ) {
                val keyboardHeight = 52.dp * 3 + 8.dp * 2
                val tileGap = 8.dp
                val columns = state.option.letters
                val rows = GameRules.MAX_ATTEMPTS
                val maxBoardWidth = minOf(maxWidth, 420.dp)
                val isWideLayout = maxWidth >= 600.dp

                if (isWideLayout) {
                    val widthTileSize = (maxBoardWidth - tileGap * (columns - 1)) / columns
                    val safeHeight = if (scaleToFit) {
                        (maxHeight - bottomInset).coerceAtLeast(0.dp)
                    } else {
                        maxHeight
                    }
                    val heightTileSize = if (safeHeight > tileGap * (rows - 1)) {
                        (safeHeight - tileGap * (rows - 1)) / rows
                    } else {
                        0.dp
                    }
                    val tileSize = if (scaleToFit && heightTileSize > 0.dp) {
                        minOf(widthTileSize, heightTileSize)
                    } else {
                        widthTileSize
                    }
                    val boardWidth = tileSize * columns + tileGap * (columns - 1)
                    val boardHeight = tileSize * rows + tileGap * (rows - 1)
                    val leftScale = if (scaleToFit && maxHeight.value > 0f) {
                        (safeHeight.value / (boardHeight + 16.dp).value).coerceAtMost(1f)
                    } else {
                        1f
                    }
                    val keyboardBottomPadding = if (scaleToFit) 8.dp + bottomInset else bottomInset
                    val keyboardTargetHeight = keyboardHeight + keyboardBottomPadding
                    val keyboardScale = if (scaleToFit && maxHeight.value > 0f) {
                        (maxHeight.value / keyboardTargetHeight.value).coerceAtMost(1f)
                    } else {
                        1f
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .fillMaxHeight()
                                .graphicsLayer(
                                    scaleX = leftScale,
                                    scaleY = leftScale,
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                GameBoard(
                                    state = state,
                                    modifier = Modifier
                                        .width(boardWidth)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.8f, fill = true)
                                .fillMaxHeight()
                                .graphicsLayer(
                                    scaleX = keyboardScale,
                                    scaleY = keyboardScale,
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                )
                                .padding(bottom = keyboardBottomPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GameKeyboard(
                                keyboardState = state.keyboard,
                                onLetter = onLetter,
                                onDelete = onDelete,
                                onSubmit = onSubmit,
                                enabled = !state.isComplete && !state.isLoadingWord,
                                modifier = Modifier.widthIn(max = 520.dp)
                            )
                        }
                    }
                } else {
                    val keyboardGap = if (scaleToFit) 8.dp else 0.dp
                    val reservedKeyboardHeight = keyboardHeight + 16.dp + keyboardGap + bottomInset
                    val availableForBoard = (maxHeight - reservedKeyboardHeight).coerceAtLeast(0.dp)
                    val widthTileSize = (maxBoardWidth - tileGap * (columns - 1)) / columns
                    val heightTileSize = if (availableForBoard > tileGap * (rows - 1)) {
                        (availableForBoard - tileGap * (rows - 1)) / rows
                    } else {
                        0.dp
                    }
                    val tileSize = if (scaleToFit && heightTileSize > 0.dp) {
                        minOf(widthTileSize, heightTileSize)
                    } else {
                        widthTileSize
                    }
                    val boardWidth = tileSize * columns + tileGap * (columns - 1)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (scaleToFit) Modifier.fillMaxHeight() else Modifier),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            GameBoard(
                                state = state,
                                modifier = Modifier
                                    .width(boardWidth)
                            )
                        }
                        if (scaleToFit) {
                            Spacer(modifier = Modifier.height(keyboardGap))
                            Spacer(modifier = Modifier.weight(1f, fill = true))
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            GameKeyboard(
                                keyboardState = state.keyboard,
                                onLetter = onLetter,
                                onDelete = onDelete,
                                onSubmit = onSubmit,
                                enabled = !state.isComplete && !state.isLoadingWord,
                                modifier = Modifier
                                    .widthIn(max = 520.dp)
                                    .padding(bottom = bottomInset)
                            )
                        }
                    }
                }
            }
        }
        if (state.isComplete) {
            CompletionPopup(
                isWin = state.isWin,
                answer = state.answerToReveal,
                guessCount = state.guesses.size,
                details = state.wordDetails,
                onNewGame = onNewGame,
                maxHeight = popupMaxHeight
            )
        }
    }
}

@Composable
private fun WordLengthSelector(
    selected: WordLengthOption,
    onSelect: (WordLengthOption) -> Unit,
    enabled: Boolean
) {
    val appTheme = LocalAppTheme.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WordLengthOption.entries.forEach { option ->
            val isSelected = option == selected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.02f else 1f,
                label = "length-scale"
            )
            val scheme = MaterialTheme.colorScheme
            val background = if (isSelected) {
                scheme.primary
            } else {
                scheme.surfaceVariant.copy(alpha = if (appTheme.style == ThemeStyle.Glass) 0.75f else 0.9f)
            }
            val border = if (isSelected) {
                scheme.secondary
            } else {
                scheme.onSurface.copy(alpha = if (appTheme.style == ThemeStyle.Glass) 0.25f else 0.2f)
            }
            val shape = RoundedCornerShape(
                when (appTheme.style) {
                    ThemeStyle.Glass -> 18.dp
                    ThemeStyle.Retro -> 6.dp
                    ThemeStyle.Image -> 12.dp
                }
            )
            val borderWidth = if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .clip(shape)
                    .background(background)
                    .border(borderWidth, border, shape)
                    .clickable(enabled = enabled) { onSelect(option) }
                    .padding(vertical = 8.dp)
                    .alpha(if (enabled) 1f else 0.5f)
                    .then(Modifier)
                    .then(Modifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) scheme.onPrimary else scheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun GameBoard(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(GameRules.MAX_ATTEMPTS) { rowIndex ->
            val feedback = when {
                state.guesses.size > rowIndex -> state.guesses[rowIndex].feedback
                state.guesses.size == rowIndex -> buildPendingFeedback(state.currentGuess, state.option.letters)
                else -> buildPendingFeedback("", state.option.letters)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                feedback.forEach { letter ->
                    LetterTile(
                        feedback = letter,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterTile(
    feedback: LetterFeedback,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(
        when (appTheme.style) {
            ThemeStyle.Glass -> 12.dp
            ThemeStyle.Retro -> 4.dp
            ThemeStyle.Image -> 10.dp
        }
    )
    val emptyBase = when (appTheme.style) {
        ThemeStyle.Glass -> scheme.surface.copy(alpha = if (isDark) 0.45f else 0.6f)
        ThemeStyle.Retro -> scheme.surfaceVariant.copy(alpha = 0.9f)
        ThemeStyle.Image -> scheme.surfaceVariant.copy(alpha = if (isDark) 0.8f else 0.85f)
    }
    val target = when (feedback.status) {
        LetterStatus.Correct -> GreenCorrect
        LetterStatus.Present -> YellowPresent
        LetterStatus.Absent -> GrayAbsent
        LetterStatus.Empty -> emptyBase
    }
    val animatedColor by animateColorAsState(targetValue = target, label = "tile-color")
    val borderColor = if (feedback.status == LetterStatus.Empty) {
        scheme.onSurface.copy(alpha = if (isDark) 0.2f else 0.25f)
    } else {
        when (appTheme.style) {
            ThemeStyle.Glass -> Color.White.copy(alpha = 0.3f)
            ThemeStyle.Retro -> Color.Black.copy(alpha = 0.35f)
            ThemeStyle.Image -> scheme.onSurface.copy(alpha = 0.25f)
        }
    }
    val borderWidth = if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp
    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = modifier,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
                .border(borderWidth, borderColor, shape),
            contentAlignment = Alignment.Center
        ) {
            val display = if (feedback.letter == ' ') "" else feedback.letter.toString()
            Text(
                text = display,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (feedback.status == LetterStatus.Empty) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    Color(0xFF0B0F14)
                }
            )
        }
    }
}

@Composable
private fun CompletionCard(
    isWin: Boolean,
    answer: String?,
    guessCount: Int,
    onNewGame: () -> Unit,
    useGlass: Boolean = true
) {
    val content: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (isWin) "You solved it!" else "Better luck next time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (isWin) {
                Text(text = "Solved in $guessCount guesses.")
            } else if (answer != null) {
                Text(text = "The word was $answer.")
            } else {
                Text(text = "All ${GameRules.MAX_ATTEMPTS} attempts used.")
            }
            Button(onClick = onNewGame, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(text = "Play another round")
            }
        }
    }
    if (useGlass) {
        GlassSurface(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun WordMeaningCard(
    answer: String,
    details: WordDetails?,
    useGlass: Boolean = true
) {
    val content: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = answer,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            val meanings = details?.meanings
                ?.filter { it.definitions.isNotEmpty() || !it.partOfSpeech.isNullOrBlank() }
                .orEmpty()
            if (meanings.isNotEmpty()) {
                meanings.forEachIndexed { index, meaning ->
                    val label = meaning.partOfSpeech?.takeIf { it.isNotBlank() }?.replaceFirstChar { ch ->
                        if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                    } ?: "Meaning ${index + 1}"
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val definitions = meaning.definitions.take(2)
                    if (definitions.isNotEmpty()) {
                        definitions.forEach { definition ->
                            Text(
                                text = "• $definition",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        Text(
                            text = "We couldn't find a reliable definition for this word.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    meaning.examples.firstOrNull()?.let { example ->
                        Text(
                            text = "Example: $example",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            } else {
                val partOfSpeech = details?.partOfSpeech?.takeIf { it.isNotBlank() }
                val partOfSpeechLabel = partOfSpeech?.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
                } ?: "Unknown family"
                Text(
                    text = "Word family: $partOfSpeechLabel",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = details?.definition ?: "We couldn't find a reliable definition for this word.",
                    style = MaterialTheme.typography.bodyLarge
                )
                val example = details?.example
                if (!example.isNullOrBlank()) {
                    Text(
                        text = "Example: $example",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
    if (useGlass) {
        GlassSurface(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun CompletionPopup(
    isWin: Boolean,
    answer: String?,
    guessCount: Int,
    details: WordDetails?,
    onNewGame: () -> Unit,
    maxHeight: Dp
) {
    val scrollState = rememberScrollState()
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scrimColor = Color.Transparent
    val popupOverlay = when (appTheme.style) {
        ThemeStyle.Glass -> {
            val base = if (isDark) appTheme.overlayDark else appTheme.overlayLight
            base.copy(alpha = if (isDark) 0.78f else 0.88f)
        }
        ThemeStyle.Retro -> if (isDark) {
            Color(0xFF0A0F16).copy(alpha = 0.86f)
        } else {
            Color(0xFFE2E8F0).copy(alpha = 0.94f)
        }
        ThemeStyle.Image -> {
            val base = if (isDark) appTheme.overlayDark else appTheme.overlayLight
            base.copy(alpha = if (isDark) 0.82f else 0.9f)
        }
    }
    val popupBorder = when (appTheme.style) {
        ThemeStyle.Glass -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.3f else 0.4f)
        ThemeStyle.Retro -> if (isDark) {
            Color.White.copy(alpha = 0.28f)
        } else {
            Color(0xFF90A4B8).copy(alpha = 0.5f)
        }
        ThemeStyle.Image -> MaterialTheme.colorScheme.secondary.copy(alpha = if (isDark) 0.3f else 0.4f)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scrimColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .heightIn(max = maxHeight),
            cornerRadius = 24.dp,
            overlayColor = popupOverlay,
            borderColorOverride = popupBorder,
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompletionCard(
                    isWin = isWin,
                    answer = answer,
                    guessCount = guessCount,
                    onNewGame = onNewGame,
                    useGlass = false
                )
                if (answer != null) {
                    WordMeaningCard(
                        answer = answer,
                        details = details,
                        useGlass = false
                    )
                }
            }
        }
    }
}

private sealed interface KeySpec {
    data class Letter(val char: Char) : KeySpec
    data object Enter : KeySpec
    data object Delete : KeySpec
}

@Composable
private fun GameKeyboard(
    keyboardState: Map<Char, LetterStatus>,
    onLetter: (Char) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val rows = listOf(
        firstRow.map { KeySpec.Letter(it) },
        secondRow.map { KeySpec.Letter(it) },
        listOf(KeySpec.Enter) + thirdRow.map { KeySpec.Letter(it) } + listOf(KeySpec.Delete)
    )
    val actionBackground = when (appTheme.style) {
        ThemeStyle.Image -> scheme.tertiary
        ThemeStyle.Glass -> scheme.primary
        ThemeStyle.Retro -> scheme.primary
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().then(modifier)) {
        val containerWidth = maxWidth
        val gap = 6.dp
        val keyHeight = 52.dp
        val maxKeys = 10
        val baseKeyWidth = (containerWidth - gap * (maxKeys - 1)) / maxKeys
        val actionKeyWidth = baseKeyWidth * 1.5f

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { row ->
                val rowKeyWidth = row.fold(0.dp) { acc, key ->
                    acc + when (key) {
                        is KeySpec.Letter -> baseKeyWidth
                        KeySpec.Enter, KeySpec.Delete -> actionKeyWidth
                    }
                }
                val rowGap = if (row.size > 1) {
                    ((containerWidth - rowKeyWidth) / (row.size - 1)).coerceAtLeast(0.dp)
                } else {
                    0.dp
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEachIndexed { index, key ->
                        when (key) {
                            is KeySpec.Letter -> {
                                val (bg, fg) = keyColorsForStatus(keyboardState[key.char])
                                KeyButton(
                                    label = key.char.toString(),
                                    background = bg,
                                    contentColor = fg,
                                    modifier = Modifier
                                        .width(baseKeyWidth)
                                        .height(keyHeight),
                                    onClick = { onLetter(key.char) },
                                    enabled = enabled
                                )
                            }
                            KeySpec.Enter -> KeyButton(
                                label = "",
                                background = actionBackground,
                                contentColor = scheme.onPrimary,
                                modifier = Modifier
                                    .width(actionKeyWidth)
                                    .height(keyHeight),
                                onClick = onSubmit,
                                enabled = enabled,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Enter"
                                    )
                                }
                            )
                            KeySpec.Delete -> KeyButton(
                                label = "",
                                background = actionBackground,
                                contentColor = scheme.onPrimary,
                                modifier = Modifier
                                    .width(actionKeyWidth)
                                    .height(keyHeight),
                                onClick = onDelete,
                                enabled = enabled,
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                        contentDescription = "Delete"
                                    )
                                }
                            )
                        }
                        if (index != row.lastIndex) {
                            Spacer(modifier = Modifier.width(rowGap))
                        }
                    }
                }
            }
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val rotation: Float,
    val sway: Float
)

@Composable
private fun ConfettiBurst(
    trigger: Int,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pieces = remember(trigger) {
        val colors = listOf(
            Color(0xFF2DD4BF),
            Color(0xFF60A5FA),
            Color(0xFFF59E0B),
            Color(0xFFF472B6),
            Color(0xFFA78BFA)
        )
        val random = Random(trigger)
        List(36) {
            ConfettiPiece(
                x = random.nextFloat(),
                size = random.nextFloat() * 10f + 8f,
                color = colors[random.nextInt(colors.size)],
                speed = random.nextFloat() * 0.45f + 0.55f,
                rotation = random.nextFloat() * 360f,
                sway = random.nextFloat() * 0.6f - 0.3f
            )
        }
    }
    val progress = remember(trigger) { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        pieces.forEach { piece ->
            val x = piece.x * width + piece.sway * width * (progress.value - 0.5f) * 0.25f
            val y = -piece.size + (height + piece.size * 2f) * progress.value * piece.speed
            val alpha = (1f - progress.value).coerceIn(0f, 1f)
            rotate(piece.rotation + 360f * progress.value, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = piece.color.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(piece.size, piece.size * 0.6f),
                    cornerRadius = CornerRadius(piece.size / 3f, piece.size / 3f)
                )
            }
        }
    }
}

@Composable
private fun keyColorsForStatus(status: LetterStatus?): Pair<Color, Color> {
    val isDark = LocalIsDarkTheme.current
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        LetterStatus.Correct -> GreenCorrect to Color(0xFF0B0F14)
        LetterStatus.Present -> YellowPresent to Color(0xFF0B0F14)
        LetterStatus.Absent -> GrayAbsent to Color(0xFF0B0F14)
        LetterStatus.Empty, null -> {
            val idleBackground = when (appTheme.style) {
                ThemeStyle.Glass -> scheme.surface.copy(alpha = if (isDark) 0.45f else 0.6f)
                ThemeStyle.Retro -> scheme.surfaceVariant.copy(alpha = 0.9f)
                ThemeStyle.Image -> scheme.surfaceVariant.copy(alpha = if (isDark) 0.8f else 0.85f)
            }
            idleBackground to scheme.onSurface
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val appTheme = LocalAppTheme.current
    val scheme = MaterialTheme.colorScheme
    val animatedColor by animateColorAsState(targetValue = background, label = "key-color")
    val shape = RoundedCornerShape(
        when (appTheme.style) {
            ThemeStyle.Glass -> 14.dp
            ThemeStyle.Retro -> 4.dp
            ThemeStyle.Image -> 10.dp
        }
    )
    val borderWidth = if (appTheme.style == ThemeStyle.Glass) 1.dp else 2.dp
    val borderColor = when (appTheme.style) {
        ThemeStyle.Glass -> Color.White.copy(alpha = 0.3f)
        ThemeStyle.Retro -> Color.Black.copy(alpha = 0.35f)
        ThemeStyle.Image -> scheme.onSurface.copy(alpha = 0.2f)
    }
    Surface(
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 0.dp,
        modifier = modifier.alpha(if (enabled) 1f else 0.4f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
                .border(borderWidth, borderColor, shape)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    icon()
                }
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatsSheet(
    state: StatsUiState,
    wordLength: Int,
    isCloudEnabled: Boolean,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${wordLength}-Letter Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(
                onClick = onRefresh,
                enabled = !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Refresh"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Refresh")
            }
        }
        AssistChip(
            onClick = {},
            enabled = false,
            label = {
                Text(
                    text = if (isCloudEnabled) {
                        "Synced securely to Firebase"
                    } else {
                        "Offline-only stats (not yet synced)"
                    }
                )
            }
        )
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.stats != null -> {
                StatsOverview(state.stats)
                GuessDistribution(state.stats)
                RecentGames(state.stats.recentGames)
            }
            else -> {
                Text(
                    text = state.error ?: "Play a game to build your stats.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
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
            val winPercent = (stats.winRate * 100).toInt()
            val lossPercent = (stats.lossRate * 100).toInt()
            StatCard(
                title = "Win %",
                value = "$winPercent%",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Loss %",
                value = "$lossPercent%",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Current Streak",
                value = stats.currentStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Best Streak",
                value = stats.bestStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Losses",
                value = stats.losses.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        StatCard(
            title = "Average Guesses",
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
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
            style = MaterialTheme.typography.titleMedium,
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
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (value > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (records.isEmpty()) {
            Text(text = "Play a few rounds to populate this list.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records) { record ->
                    RecentGameRow(record)
                }
            }
        }
    }
}

@Composable
private fun RecentGameRow(record: GameRecord) {
    val statusColor = if (record.won) GreenCorrect else MaterialTheme.colorScheme.error
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatGameDate(record.playedAtEpochMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Word: ${record.word}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (record.won) "Win" else "Loss",
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (record.won) "${record.guessCount} guess(es)" else "Missed in ${GameRules.MAX_ATTEMPTS}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun buildPendingFeedback(guess: String, length: Int): List<LetterFeedback> {
    val upper = guess.uppercase(Locale.getDefault())
    return (0 until length).map { index ->
        val letter = upper.getOrNull(index) ?: ' '
        LetterFeedback(letter = letter, status = LetterStatus.Empty)
    }
}

private fun formatGameDate(timestamp: Long): String {
    val date = Date(timestamp)
    return DateFormat.format("MMM d • h:mm a", date).toString()
}
