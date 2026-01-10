package com.example.wordgame.stats

import com.example.wordgame.logic.GameRules

data class GameResult(
    val word: String,
    val wordLength: Int,
    val guessCount: Int,
    val won: Boolean,
    val playedAtEpochMillis: Long = System.currentTimeMillis()
)

data class GameRecord(
    val id: String,
    val word: String,
    val wordLength: Int,
    val guessCount: Int,
    val won: Boolean,
    val playedAtEpochMillis: Long,
    val createdByCloud: Boolean = true
)

data class GameStats(
    val wordLength: Int,
    val totalGames: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val lossRate: Double,
    val currentStreak: Int,
    val bestStreak: Int,
    val averageGuesses: Double,
    val guessDistribution: Map<Int, Int>,
    val misses: Int,
    val recentGames: List<GameRecord>
) {
    companion object {
        fun fromRecords(wordLength: Int, records: List<GameRecord>): GameStats {
            val filtered = records
                .filter { it.wordLength == wordLength }
                .sortedByDescending { it.playedAtEpochMillis }

            val totalGames = filtered.size
            val wins = filtered.count { it.won }
            val losses = totalGames - wins
            val winRate = if (totalGames == 0) 0.0 else wins.toDouble() / totalGames.toDouble()
            val lossRate = if (totalGames == 0) 0.0 else losses.toDouble() / totalGames.toDouble()
            val averageGuesses = if (wins == 0) 0.0 else filtered.filter { it.won }.sumOf { it.guessCount }.toDouble() / wins.toDouble()

            val distribution = buildMap<Int, Int> {
                for (attempt in 1..GameRules.MAX_ATTEMPTS) {
                    put(attempt, 0)
                }
                filtered.filter { it.won }.forEach { record ->
                    val key = record.guessCount.coerceIn(1, GameRules.MAX_ATTEMPTS)
                    put(key, (get(key) ?: 0) + 1)
                }
            }
            val misses = filtered.count { !it.won }

            var bestStreak = 0
            var currentStreak = 0
            filtered.forEach { record ->
                if (record.won) {
                    currentStreak++
                    if (currentStreak > bestStreak) bestStreak = currentStreak
                } else {
                    currentStreak = 0
                }
            }

            val recent = filtered.take(10)

            return GameStats(
                wordLength = wordLength,
                totalGames = totalGames,
                wins = wins,
                losses = losses,
                winRate = winRate,
                lossRate = lossRate,
                currentStreak = currentStreak,
                bestStreak = bestStreak,
                averageGuesses = averageGuesses,
                guessDistribution = distribution,
                misses = misses,
                recentGames = recent
            )
        }

        val empty = GameStats(
            wordLength = 5,
            totalGames = 0,
            wins = 0,
            losses = 0,
            winRate = 0.0,
            lossRate = 0.0,
            currentStreak = 0,
            bestStreak = 0,
            averageGuesses = 0.0,
            guessDistribution = (1..GameRules.MAX_ATTEMPTS).associateWith { 0 },
            misses = 0,
            recentGames = emptyList()
        )
    }
}
