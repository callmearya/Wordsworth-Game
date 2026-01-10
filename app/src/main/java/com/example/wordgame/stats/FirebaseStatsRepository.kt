package com.example.wordgame.stats

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseStatsRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StatsRepository {

    override val isCloudEnabled: Boolean = true

    override suspend fun recordGame(result: GameResult) {
        withContext(dispatcher) {
            val user = ensureUser()
            val document = firestore.collection("users")
                .document(user.uid)
                .collection("gameRecords")
                .document()

            document.set(result.toPayload()).await()
        }
    }

    override suspend fun loadStats(wordLength: Int): GameStats = withContext(dispatcher) {
        val user = ensureUser()
        val snapshot = firestore.collection("users")
            .document(user.uid)
            .collection("gameRecords")
            .orderBy("playedAtEpochMillis", Query.Direction.DESCENDING)
            .limit(150)
            .get()
            .await()

        val records = snapshot.documents.mapNotNull { it.toGameRecord() }
        GameStats.fromRecords(wordLength, records)
    }

    private suspend fun ensureUser(): FirebaseUser {
        val existing = auth.currentUser
        if (existing != null) {
            return existing
        }
        return auth.signInAnonymously().await().user
            ?: error("FirebaseAuth did not return a user")
    }

    private fun GameResult.toPayload(): Map<String, Any> = mapOf(
        "word" to word,
        "wordLength" to wordLength,
        "guessCount" to guessCount,
        "won" to won,
        "playedAtEpochMillis" to playedAtEpochMillis
    )

    private fun DocumentSnapshot.toGameRecord(): GameRecord? {
        val word = getString("word") ?: return null
        val wordLength = getLong("wordLength")?.toInt() ?: word.length
        val guessCount = getLong("guessCount")?.toInt() ?: 0
        val won = getBoolean("won") ?: false
        val playedAt = getLong("playedAtEpochMillis") ?: 0L
        return GameRecord(
            id = id,
            word = word,
            wordLength = wordLength,
            guessCount = guessCount,
            won = won,
            playedAtEpochMillis = playedAt,
            createdByCloud = true
        )
    }
}
