package com.example.wordgame.data

import com.example.wordgame.data.remote.RemoteWordDataSource

class WordRepository(
    private val remote: RemoteWordDataSource
) {

    suspend fun newChallenge(option: WordLengthOption): WordChallenge {
        return remote.fetchWord(option)
            ?: error("Unable to fetch a word from the remote APIs.")
    }

    fun isValidGuess(option: WordLengthOption, guess: String): Boolean {
        if (guess.length != option.letters) return false
        return guess.all { it.isLetter() }
    }
}
