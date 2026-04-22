package com.example.wordgame.data

import android.content.Context
import com.example.wordgame.data.remote.RemoteWordDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyWordsRepository(
    context: Context,
    private val remote: RemoteWordDataSource,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun getDailyWords(count: Int = DEFAULT_COUNT): List<WordInsight> =
        withContext(dispatcher) {
            val today = todayStamp()
            val cachedDate = prefs.getString(KEY_DATE, null)
            val cachedPayload = prefs.getString(KEY_PAYLOAD, null)
            if (cachedDate == today && !cachedPayload.isNullOrBlank()) {
                val cached = runCatching {
                    json.decodeFromString<List<WordInsight>>(cachedPayload)
                }.getOrNull().orEmpty()
                if (cached.isNotEmpty()) {
                    return@withContext cached
                }
            }

            val words = remote.fetchRandomWords(count = count)
            if (words.isEmpty()) return@withContext emptyList()

            val uniqueWords = words.map { it.lowercase(Locale.getDefault()) }.distinct()
            val insights = coroutineScope {
                uniqueWords.map { word ->
                    async {
                        remote.fetchWordInsight(word)
                            ?: WordInsight(word = word)
                    }
                }.awaitAll()
            }

            val trimmed = insights
                .distinctBy { it.word.lowercase(Locale.getDefault()) }
                .take(count)

            prefs.edit()
                .putString(KEY_DATE, today)
                .putString(KEY_PAYLOAD, json.encodeToString(trimmed))
                .apply()

            trimmed
        }

    private fun todayStamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date())
    }

    private companion object {
        private const val PREFS_NAME = "daily_words_cache"
        private const val KEY_DATE = "daily_words_date"
        private const val KEY_PAYLOAD = "daily_words_payload"
        private const val DEFAULT_COUNT = 5
    }
}
