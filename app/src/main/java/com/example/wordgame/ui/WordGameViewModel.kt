package com.example.wordgame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordgame.WordGameApp
import com.example.wordgame.data.WordDetails
import com.example.wordgame.data.WordLengthOption
import com.example.wordgame.data.WordRepository
import com.example.wordgame.logic.GameRules
import com.example.wordgame.logic.GuessEvaluation
import com.example.wordgame.logic.LetterStatus
import com.example.wordgame.logic.WordleEngine
import com.example.wordgame.stats.GameResult
import com.example.wordgame.stats.GameStats
import com.example.wordgame.stats.StatsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val option: WordLengthOption = WordLengthOption.default,
    val guesses: List<GuessEvaluation> = emptyList(),
    val currentGuess: String = "",
    val keyboard: Map<Char, LetterStatus> = emptyMap(),
    val isComplete: Boolean = false,
    val isWin: Boolean = false,
    val revealAnswer: Boolean = false,
    val answerToReveal: String? = null,
    val wordDetails: WordDetails? = null,
    val showStats: Boolean = false,
    val statsState: StatsUiState = StatsUiState(isLoading = true),
    val userMessage: String? = null,
    val isCloudEnabled: Boolean = false,
    val isLoadingWord: Boolean = false
)

data class StatsUiState(
    val isLoading: Boolean = false,
    val stats: GameStats? = null,
    val error: String? = null
)

sealed interface GameUiEvent {
    data class Snackbar(val message: String) : GameUiEvent
    data class RoundComplete(val won: Boolean) : GameUiEvent
}

class WordGameViewModel(
    private val wordRepository: WordRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            option = WordLengthOption.default,
            statsState = StatsUiState(isLoading = true),
            isCloudEnabled = statsRepository.isCloudEnabled
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameUiEvent>()
    val events = _events.asSharedFlow()

    private val maxAttempts = GameRules.MAX_ATTEMPTS
    private var answer: String = ""
    private var currentDetails: WordDetails? = null
    private var resultRecorded = false

    init {
        startNewGame(WordLengthOption.default, shouldReloadStats = true)
    }

    fun onLetterInput(letter: Char) {
        val state = _uiState.value
        if (state.isComplete || state.isLoadingWord || !letter.isLetter()) return
        if (state.currentGuess.length >= state.option.letters) return

        val updatedGuess = state.currentGuess + letter.uppercaseChar()
        _uiState.update { it.copy(currentGuess = updatedGuess, userMessage = null) }
    }

    fun onBackspace() {
        val state = _uiState.value
        if (state.currentGuess.isEmpty() || state.isComplete || state.isLoadingWord) return
        _uiState.update { it.copy(currentGuess = state.currentGuess.dropLast(1)) }
    }

    fun onSubmitGuess() {
        val state = _uiState.value
        if (state.isComplete || state.isLoadingWord) return

        val guess = state.currentGuess.uppercase()
        if (guess.length != state.option.letters) {
            setUserMessage("Need ${state.option.letters} letters")
            return
        }
        if (!wordRepository.isValidGuess(state.option, guess)) {
            setUserMessage("Use letters only")
            return
        }

        val evaluation = WordleEngine.evaluate(guess, answer)
        val updatedGuesses = (state.guesses + evaluation).takeLast(maxAttempts)
        val updatedKeyboard = state.keyboard.toMutableMap()
        evaluation.feedback.forEach { feedback ->
            updatedKeyboard[feedback.letter] =
                WordleEngine.mergeStatus(updatedKeyboard[feedback.letter], feedback.status)
        }

        val isWin = guess == answer
        val isComplete = isWin || updatedGuesses.size >= maxAttempts
        val revealAnswer = isComplete && !isWin

        _uiState.update {
            it.copy(
                guesses = updatedGuesses,
                currentGuess = "",
                keyboard = updatedKeyboard,
                isComplete = isComplete,
                isWin = isWin,
                revealAnswer = revealAnswer,
                answerToReveal = if (isComplete) answer else it.answerToReveal,
                userMessage = if (isWin) "Great job!" else it.userMessage,
                wordDetails = currentDetails
            )
        }

        if (isComplete) {
            recordResult(
                guessCount = updatedGuesses.size,
                won = isWin
            )
            viewModelScope.launch {
                _events.emit(GameUiEvent.RoundComplete(isWin))
            }
        }
    }

    fun onModeSelected(option: WordLengthOption) {
        if (_uiState.value.option == option) return
        startNewGame(option, shouldReloadStats = true)
    }

    fun onNewGameRequested() {
        startNewGame(_uiState.value.option, shouldReloadStats = false)
    }

    fun onStatsVisibilityChange(show: Boolean) {
        _uiState.update { it.copy(showStats = show) }
        if (show) {
            refreshStats(force = false)
        }
    }

    fun consumeUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun refreshStats(
        force: Boolean = true,
        lengthOption: WordLengthOption = _uiState.value.option
    ) {
        if (!force && _uiState.value.statsState.stats != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    statsState = it.statsState.copy(
                        isLoading = true,
                        error = null
                    )
                )
            }
            try {
                val stats = statsRepository.loadStats(lengthOption.letters)
                _uiState.update {
                    it.copy(
                        statsState = StatsUiState(
                            isLoading = false,
                            stats = stats,
                            error = null
                        )
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        statsState = it.statsState.copy(
                            isLoading = false,
                            error = error.message ?: "Unable to load statistics"
                        )
                    )
                }
                _events.emit(
                    GameUiEvent.Snackbar("Unable to reach the stats service. Working offline.")
                )
            }
        }
    }

    private fun startNewGame(option: WordLengthOption, shouldReloadStats: Boolean) {
        viewModelScope.launch {
            prepareNewGame(option, shouldReloadStats)
        }
    }

    private suspend fun prepareNewGame(option: WordLengthOption, shouldReloadStats: Boolean) {
        resultRecorded = false
        answer = ""
        currentDetails = null
        _uiState.update {
            it.copy(
                option = option,
                guesses = emptyList(),
                currentGuess = "",
                keyboard = emptyMap(),
                isComplete = false,
                isWin = false,
                revealAnswer = false,
                answerToReveal = null,
                showStats = false,
                statsState = if (shouldReloadStats) {
                    StatsUiState(isLoading = true)
                } else {
                    it.statsState
                },
                userMessage = null,
                isLoadingWord = true,
                wordDetails = null
            )
        }

        val challenge = runCatching { wordRepository.newChallenge(option) }.getOrNull()
        if (challenge == null) {
            _uiState.update { it.copy(isLoadingWord = false) }
            _events.emit(
                GameUiEvent.Snackbar("Unable to fetch a word right now. Check your connection.")
            )
            return
        }

        answer = challenge.word
        currentDetails = challenge.details
        _uiState.update {
            it.copy(
                isLoadingWord = false,
                wordDetails = challenge.details
            )
        }

        if (shouldReloadStats) {
            refreshStats(lengthOption = option)
        }
    }

    private fun recordResult(guessCount: Int, won: Boolean) {
        if (resultRecorded) return
        resultRecorded = true
        val result = GameResult(
            word = answer,
            wordLength = _uiState.value.option.letters,
            guessCount = guessCount,
            won = won
        )
        viewModelScope.launch {
            try {
                statsRepository.recordGame(result)
                refreshStats(force = true)
            } catch (error: Exception) {
                _events.emit(
                    GameUiEvent.Snackbar(
                        error.message ?: "Unable to store stats. They will sync later."
                    )
                )
            }
        }
    }

    private fun setUserMessage(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WordGameApp
                WordGameViewModel(
                    wordRepository = app.container.wordRepository,
                    statsRepository = app.container.statsRepository
                )
            }
        }
    }
}
