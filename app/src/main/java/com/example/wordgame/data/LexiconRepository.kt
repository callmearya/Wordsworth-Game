package com.example.wordgame.data

import com.example.wordgame.data.remote.RemoteWordDataSource

class LexiconRepository(
    private val remote: RemoteWordDataSource
) {
    suspend fun lookup(word: String): WordInsight? = remote.fetchWordInsight(word)

    suspend fun synonyms(word: String, max: Int = 12): List<String> =
        remote.fetchSynonyms(word, max)
}
