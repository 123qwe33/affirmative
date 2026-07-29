package com.geoffrogers.affirmative

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsPlayer(context: Context, private val onItemDone: () -> Unit) {
    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) { onItemDone() }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) { onItemDone() }
                })
                ready = true
            }
        }
    }

    var speechRate: Float = 1.0f

    fun speak(text: String) {
        if (ready) {
            tts?.setSpeechRate(speechRate)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val UTTERANCE_ID = "affirmation"
    }
}
