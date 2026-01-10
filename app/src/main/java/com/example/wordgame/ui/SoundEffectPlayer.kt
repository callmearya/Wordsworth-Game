package com.example.wordgame.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.wordgame.R
import java.util.concurrent.atomic.AtomicBoolean

class SoundEffectPlayer(context: Context) {
    private val soundPool: SoundPool
    private val loaded = mutableSetOf<Int>()
    private val pending = mutableSetOf<Int>()
    private val isReleased = AtomicBoolean(false)
    private val successId: Int
    private val failId: Int

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build()
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && !isReleased.get()) {
                loaded.add(sampleId)
                if (pending.remove(sampleId)) {
                    play(sampleId)
                }
            }
        }
        successId = soundPool.load(context, R.raw.success, 1)
        failId = soundPool.load(context, R.raw.fail, 1)
    }

    fun playSuccess() = playOrQueue(successId)

    fun playFail() = playOrQueue(failId)

    private fun playOrQueue(sampleId: Int) {
        if (sampleId == 0 || isReleased.get()) return
        if (loaded.contains(sampleId)) {
            play(sampleId)
        } else {
            pending.add(sampleId)
        }
    }

    private fun play(sampleId: Int) {
        soundPool.play(sampleId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        if (isReleased.compareAndSet(false, true)) {
            soundPool.release()
        }
    }
}
