package com.example.wordgame.data

import kotlinx.serialization.Serializable

@Serializable
data class WordMeaning(
    val partOfSpeech: String? = null,
    val definitions: List<String> = emptyList(),
    val examples: List<String> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)
