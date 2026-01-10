package com.example.wordgame.logic

enum class LetterStatus {
    Correct,
    Present,
    Absent,
    Empty
}

data class LetterFeedback(
    val letter: Char,
    val status: LetterStatus
)

data class GuessEvaluation(
    val guess: String,
    val feedback: List<LetterFeedback>
)

object WordleEngine {

    fun evaluate(guess: String, answer: String): GuessEvaluation {
        val target = answer.uppercase()
        val attempt = guess.uppercase()
        val length = attempt.length
        val statuses = Array(length) { LetterStatus.Absent }
        val letters = attempt.toCharArray()
        val counts = IntArray(26)

        target.forEach { char ->
            val idx = char.toAlphabetIndex()
            if (idx >= 0) counts[idx]++
        }

        for (i in 0 until length) {
            val guessChar = letters[i]
            if (i < target.length && guessChar == target[i]) {
                statuses[i] = LetterStatus.Correct
                counts[guessChar.toAlphabetIndex()]--
            }
        }

        for (i in 0 until length) {
            if (statuses[i] == LetterStatus.Correct) continue
            val guessChar = letters[i]
            val idx = guessChar.toAlphabetIndex()
            statuses[i] = if (idx >= 0 && counts[idx] > 0) {
                counts[idx]--
                LetterStatus.Present
            } else {
                LetterStatus.Absent
            }
        }

        val feedback = letters.indices.map { i ->
            LetterFeedback(
                letter = letters[i],
                status = statuses[i]
            )
        }

        return GuessEvaluation(
            guess = attempt,
            feedback = feedback
        )
    }

    fun mergeStatus(existing: LetterStatus?, incoming: LetterStatus): LetterStatus {
        if (existing == null) return incoming
        return when {
            incoming == LetterStatus.Correct -> LetterStatus.Correct
            incoming == LetterStatus.Present && existing == LetterStatus.Absent -> LetterStatus.Present
            else -> existing
        }
    }

    private fun Char.toAlphabetIndex(): Int = if (this in 'A'..'Z') {
        code - 'A'.code
    } else {
        -1
    }
}
