package com.example.wordgame.stats

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class InMemoryStatsRepository : StatsRepository {
    override val isCloudEnabled: Boolean = false
    private val mutex = Mutex()
    private val records = mutableListOf<GameRecord>()

    override suspend fun recordGame(result: GameResult) {
        mutex.withLock {
            records.add(
                GameRecord(
                    id = UUID.randomUUID().toString(),
                    word = result.word,
                    wordLength = result.wordLength,
                    guessCount = result.guessCount,
                    won = result.won,
                    playedAtEpochMillis = result.playedAtEpochMillis,
                    createdByCloud = false
                )
            )
        }
    }

    override suspend fun loadStats(wordLength: Int): GameStats =
        mutex.withLock { GameStats.fromRecords(wordLength, records.toList()) }
}
