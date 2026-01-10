package com.example.wordgame.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordgame.WordGameApp
import com.example.wordgame.data.DailyWordsRepository
import com.example.wordgame.data.LexiconRepository
import com.example.wordgame.data.WordInsight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DashboardTab {
    Dictionary,
    DailyWords,
    Play,
    Thesaurus,
    Stats
}

data class ThesaurusResult(
    val word: String,
    val partOfSpeech: String? = null,
    val definition: String? = null,
    val meanings: List<com.example.wordgame.data.WordMeaning> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

data class DashboardUiState(
    val selectedTab: DashboardTab = DashboardTab.Play,
    val dailyWords: List<WordInsight> = emptyList(),
    val isDailyLoading: Boolean = false,
    val dailyError: String? = null,
    val dictionaryQuery: String = "",
    val dictionaryResult: WordInsight? = null,
    val isDictionaryLoading: Boolean = false,
    val dictionaryError: String? = null,
    val thesaurusQuery: String = "",
    val thesaurusResult: ThesaurusResult? = null,
    val isThesaurusLoading: Boolean = false,
    val thesaurusError: String? = null
)

class DashboardViewModel(
    private val dailyWordsRepository: DailyWordsRepository,
    private val lexiconRepository: LexiconRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshDailyWords(force = false)
    }

    fun selectTab(tab: DashboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateDictionaryQuery(value: String) {
        _uiState.update { it.copy(dictionaryQuery = value, dictionaryError = null) }
    }

    fun updateThesaurusQuery(value: String) {
        _uiState.update { it.copy(thesaurusQuery = value, thesaurusError = null) }
    }

    fun refreshDailyWords(force: Boolean) {
        if (!force && _uiState.value.dailyWords.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDailyLoading = true, dailyError = null) }
            val words = runCatching { dailyWordsRepository.getDailyWords() }.getOrNull()
            if (words.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isDailyLoading = false,
                        dailyError = "Unable to load today’s words.",
                        dailyWords = emptyList()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isDailyLoading = false,
                        dailyWords = words,
                        dailyError = null
                    )
                }
            }
        }
    }

    fun searchDictionary() {
        val query = _uiState.value.dictionaryQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(dictionaryError = "Enter a word to search.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isDictionaryLoading = true, dictionaryError = null) }
            val result = runCatching { lexiconRepository.lookup(query) }.getOrNull()
            if (result == null) {
                _uiState.update {
                    it.copy(
                        isDictionaryLoading = false,
                        dictionaryResult = null,
                        dictionaryError = "No definition found."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isDictionaryLoading = false,
                        dictionaryResult = result,
                        dictionaryError = null
                    )
                }
            }
        }
    }

    fun searchThesaurus() {
        val query = _uiState.value.thesaurusQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(thesaurusError = "Enter a word to explore synonyms.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isThesaurusLoading = true, thesaurusError = null) }
            val insight = runCatching { lexiconRepository.lookup(query) }.getOrNull()
            val meanings = insight?.meanings.orEmpty()
            val synonyms = insight?.synonyms?.takeIf { it.isNotEmpty() }
                ?: runCatching { lexiconRepository.synonyms(query) }.getOrNull().orEmpty()
            val antonyms = insight?.antonyms.orEmpty()
            val hasMeaningSynonyms = meanings.any { it.synonyms.isNotEmpty() }
            val hasMeaningAntonyms = meanings.any { it.antonyms.isNotEmpty() }
            if (synonyms.isEmpty() && antonyms.isEmpty() && !hasMeaningSynonyms && !hasMeaningAntonyms) {
                _uiState.update {
                    it.copy(
                        isThesaurusLoading = false,
                        thesaurusResult = null,
                        thesaurusError = "No synonyms found."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isThesaurusLoading = false,
                        thesaurusResult = ThesaurusResult(
                            word = insight?.word ?: query,
                            partOfSpeech = insight?.partOfSpeech,
                            definition = insight?.definition,
                            meanings = meanings,
                            synonyms = synonyms,
                            antonyms = antonyms
                        ),
                        thesaurusError = null
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WordGameApp
                DashboardViewModel(
                    dailyWordsRepository = app.container.dailyWordsRepository,
                    lexiconRepository = app.container.lexiconRepository
                )
            }
        }
    }
}
