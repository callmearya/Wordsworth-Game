package com.example.wordgame.stats

interface StatsRepository {
    val isCloudEnabled: Boolean

    suspend fun recordGame(result: GameResult)

    suspend fun loadStats(wordLength: Int): GameStats
}
