package com.example.wordgame.data

data class WordDetails(
    val definition: String? = null,
    val partOfSpeech: String? = null,
    val example: String? = null,
    val meanings: List<WordMeaning> = emptyList()
)

data class WordChallenge(
    val word: String,
    val details: WordDetails?
)
