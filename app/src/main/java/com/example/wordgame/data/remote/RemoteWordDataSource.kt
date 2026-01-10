package com.example.wordgame.data.remote

import com.example.wordgame.data.WordChallenge
import com.example.wordgame.data.WordDetails
import com.example.wordgame.data.WordInsight
import com.example.wordgame.data.WordLengthOption
import com.example.wordgame.data.WordMeaning
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder
import java.util.Locale

class RemoteWordDataSource(
    private val client: OkHttpClient,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun fetchWord(option: WordLengthOption): WordChallenge? = withContext(dispatcher) {
        val word = fetchRandomWordsInternal(count = 1, length = option.letters).firstOrNull()
            ?: return@withContext null
        val details = fetchWordDetailsInternal(word)
        WordChallenge(word = word, details = details)
    }

    suspend fun fetchWordDetails(word: String): WordDetails? = withContext(dispatcher) {
        fetchWordDetailsInternal(word)
    }

    suspend fun fetchRandomWords(
        count: Int,
        length: Int? = null
    ): List<String> = withContext(dispatcher) {
        fetchRandomWordsInternal(count = count, length = length)
    }

    private suspend fun fetchWordDetailsInternal(word: String): WordDetails? {
        val lower = word.lowercase(Locale.getDefault())
        val request = Request.Builder()
            .url("https://api.dictionaryapi.dev/api/v2/entries/en/$lower")
            .build()

        return client.newCall(request).executeSafely { body ->
            val entries = runCatching {
                json.decodeFromString<List<DictionaryEntry>>(body)
            }.getOrNull().orEmpty()
            val entry = entries.firstOrNull() ?: return@executeSafely null
            val meanings = entry.meanings.map { mapMeaning(it) }
                .filter { it.definitions.isNotEmpty() || !it.partOfSpeech.isNullOrBlank() }
            val primary = meanings.firstOrNull()
            WordDetails(
                definition = primary?.definitions?.firstOrNull(),
                partOfSpeech = primary?.partOfSpeech?.trim(),
                example = primary?.examples?.firstOrNull(),
                meanings = meanings
            )
        }
    }

    suspend fun fetchWordInsight(word: String): WordInsight? = withContext(dispatcher) {
        val lower = word.lowercase(Locale.getDefault())
        val request = Request.Builder()
            .url("https://api.dictionaryapi.dev/api/v2/entries/en/$lower")
            .build()

        return@withContext client.newCall(request).executeSafely { body ->
            val entries = runCatching {
                json.decodeFromString<List<DictionaryEntry>>(body)
            }.getOrNull().orEmpty()
            val entry = entries.firstOrNull() ?: return@executeSafely null
            val meanings = entry.meanings.map { mapMeaning(it) }
                .filter { it.definitions.isNotEmpty() || !it.partOfSpeech.isNullOrBlank() }
            val primary = meanings.firstOrNull()
            val baseSynonyms = meanings.flatMap { it.synonyms }
            val baseAntonyms = meanings.flatMap { it.antonyms }
            val supplementalSynonyms = if (baseSynonyms.isEmpty()) {
                fetchSynonymsInternal(lower, max = 10)
            } else {
                emptyList()
            }
            WordInsight(
                word = entry.word?.trim()?.ifBlank { lower } ?: lower,
                definition = primary?.definitions?.firstOrNull(),
                partOfSpeech = primary?.partOfSpeech?.trim(),
                example = primary?.examples?.firstOrNull(),
                meanings = meanings,
                synonyms = (baseSynonyms + supplementalSynonyms).distinct().take(12),
                antonyms = baseAntonyms.distinct().take(8)
            )
        }
    }

    suspend fun fetchSynonyms(word: String, max: Int = 12): List<String> = withContext(dispatcher) {
        fetchSynonymsInternal(word, max)
    }

    private inline fun <T> okhttp3.Call.executeSafely(
        crossinline bodyHandler: (String) -> T?
    ): T? {
        return try {
            execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                bodyHandler(body)
            }
        } catch (error: IOException) {
            null
        }
    }

    private fun fetchRandomWordsInternal(count: Int, length: Int? = null): List<String> {
        val primaryRequest = Request.Builder()
            .url(buildRandomWordUrl(count = count, length = length))
            .build()

        val primary = client.newCall(primaryRequest).executeSafely { body ->
            parseRandomWordResponse(body)
        }.orEmpty()

        if (primary.isNotEmpty()) {
            return primary
        }

        val fallbackRequest = Request.Builder()
            .url(buildFallbackRandomWordUrl(count = count, length = length))
            .build()
        return client.newCall(fallbackRequest).executeSafely { body ->
            parseRandomWordResponse(body)
        }.orEmpty()
    }

    private fun parseRandomWordResponse(body: String): List<String> {
        val element = json.parseToJsonElement(body)
        val array = element as? JsonArray ?: return emptyList()
        return array.mapNotNull { item ->
            (item as? JsonPrimitive)?.content?.trim()
        }.filter { it.isNotBlank() }
            .map { it.uppercase(Locale.getDefault()) }
            .distinct()
    }

    private fun buildRandomWordUrl(count: Int, length: Int?): String {
        val base = "https://random-word-api.vercel.app/api?words=$count"
        return if (length != null) "$base&length=$length" else base
    }

    private fun buildFallbackRandomWordUrl(count: Int, length: Int?): String {
        val base = "https://random-word-api.herokuapp.com/word?number=$count"
        return if (length != null) "$base&length=$length" else base
    }

    private fun fetchSynonymsInternal(word: String, max: Int): List<String> {
        val encoded = runCatching {
            URLEncoder.encode(word, Charsets.UTF_8.name())
        }.getOrDefault(word)
        val request = Request.Builder()
            .url("https://api.datamuse.com/words?rel_syn=$encoded&max=$max")
            .build()

        return client.newCall(request).executeSafely { body ->
            val results = runCatching {
                json.decodeFromString<List<DatamuseEntry>>(body)
            }.getOrNull().orEmpty()
            results.mapNotNull { it.word?.trim() }
                .filter { it.isNotBlank() }
                .distinct()
        }.orEmpty()
    }

    private fun collectWords(vararg wordLists: List<String>?): List<String> =
        wordLists.asSequence()
            .filterNotNull()
            .flatten()
            .mapNotNull { it.trim().ifBlank { null } }
            .distinct()
            .toList()

    private fun mapMeaning(meaning: DictionaryMeaning): WordMeaning {
        val definitions = meaning.definitions
            .mapNotNull { it.definition?.trim()?.ifBlank { null } }
        val examples = meaning.definitions
            .mapNotNull { it.example?.trim()?.ifBlank { null } }
        val definitionSynonyms = meaning.definitions.flatMap { it.synonyms }
        val definitionAntonyms = meaning.definitions.flatMap { it.antonyms }
        val synonyms = collectWords(meaning.synonyms, definitionSynonyms)
        val antonyms = collectWords(meaning.antonyms, definitionAntonyms)
        return WordMeaning(
            partOfSpeech = meaning.partOfSpeech?.trim(),
            definitions = definitions,
            examples = examples,
            synonyms = synonyms,
            antonyms = antonyms
        )
    }

    @Serializable
    private data class DictionaryEntry(
        val word: String? = null,
        val meanings: List<DictionaryMeaning> = emptyList()
    )

    @Serializable
    private data class DictionaryMeaning(
        @SerialName("partOfSpeech")
        val partOfSpeech: String? = null,
        val definitions: List<DictionaryDefinition> = emptyList(),
        val synonyms: List<String> = emptyList(),
        val antonyms: List<String> = emptyList()
    )

    @Serializable
    private data class DictionaryDefinition(
        val definition: String? = null,
        val example: String? = null,
        val synonyms: List<String> = emptyList(),
        val antonyms: List<String> = emptyList()
    )

    @Serializable
    private data class DatamuseEntry(
        val word: String? = null,
        val score: Int? = null
    )
}
