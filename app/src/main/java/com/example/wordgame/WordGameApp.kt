package com.example.wordgame

import android.app.Application
import com.example.wordgame.data.AppContainer
import com.example.wordgame.data.FirebaseInitializer

class WordGameApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.init(this)
        container = AppContainer(this)
    }
}
