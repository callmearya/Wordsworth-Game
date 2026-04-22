package com.example.wordgame.data

enum class WordLengthOption(val letters: Int, val label: String) {
    FOUR(4, "4 Letters"),
    FIVE(5, "5 Letters");

    companion object {
        fun fromLetters(count: Int): WordLengthOption =
            entries.firstOrNull { it.letters == count } ?: FIVE

        val default = FIVE
    }
}
