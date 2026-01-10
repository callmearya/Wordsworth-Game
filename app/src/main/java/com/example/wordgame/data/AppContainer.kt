package com.example.wordgame.data

import android.content.Context
import com.example.wordgame.data.remote.RemoteWordDataSource
import com.example.wordgame.stats.FirebaseStatsRepository
import com.example.wordgame.stats.InMemoryStatsRepository
import com.example.wordgame.stats.StatsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        builder.addInterceptor(logging)
        builder.build()
    }

    private val remoteWordDataSource = RemoteWordDataSource(
        client = okHttpClient,
        json = json
    )

    val wordRepository = WordRepository(remoteWordDataSource)
    val lexiconRepository = LexiconRepository(remoteWordDataSource)
    val dailyWordsRepository = DailyWordsRepository(appContext, remoteWordDataSource, json)

    val statsRepository: StatsRepository =
        if (FirebaseInitializer.isCloudAvailable(context)) {
            FirebaseStatsRepository(
                auth = FirebaseAuth.getInstance(),
                firestore = FirebaseFirestore.getInstance()
            )
        } else {
            InMemoryStatsRepository()
        }
}
