package com.example.wordgame.data

import android.content.Context
import com.example.wordgame.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {

    fun init(context: Context) {
        if (FirebaseApp.getApps(context).isNotEmpty()) return

        runCatching { FirebaseApp.initializeApp(context) }.getOrNull()?.let { return }

        if (!hasManualConfig()) return

        val optionsBuilder = FirebaseOptions.Builder()
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)

        if (BuildConfig.FIREBASE_SENDER_ID.isNotBlank()) {
            optionsBuilder.setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
        }
        if (BuildConfig.FIREBASE_STORAGE_BUCKET.isNotBlank()) {
            optionsBuilder.setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
        }

        FirebaseApp.initializeApp(context, optionsBuilder.build())
    }

    fun isCloudAvailable(context: Context): Boolean = FirebaseApp.getApps(context).isNotEmpty()

    private fun hasManualConfig(): Boolean =
        BuildConfig.FIREBASE_API_KEY.isNotBlank() &&
            BuildConfig.FIREBASE_APP_ID.isNotBlank() &&
            BuildConfig.FIREBASE_PROJECT_ID.isNotBlank()
}
