package com.example.wordgame.data

import kotlinx.serialization.Serializable

@Serializable
data class WordInsight(
    val word: String,
    val definition: String? = null,
    val partOfSpeech: String? = null,
    val example: String? = null,
    val meanings: List<WordMeaning> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)
