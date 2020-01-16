package ru.cscenter.fingerpaint.service

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.*


class AudioController(context: Context) : TextToSpeech.OnInitListener {
    private val textToSpeech = TextToSpeech(context, this)
    var isAvailable = true
        private set

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale(LANGUAGE)
            if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.language = locale
            } else {
                isAvailable = false
            }
            textToSpeech.setPitch(PITCH_RATE)
            textToSpeech.setSpeechRate(SPEECH_RATE)
        } else {
            isAvailable = false
        }
    }

    fun speak(text: String) {
        if (!isAvailable) {
            return
        }
        val utteranceId = text.hashCode().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            val parameters = hashMapOf(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID to utteranceId)
            @Suppress("DEPRECATION")
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, parameters)
        }
    }

    fun shutdown() {
        textToSpeech.shutdown()
    }

    companion object {
        private const val PITCH_RATE = 1.3f
        private const val SPEECH_RATE = 0.8f
        private const val LANGUAGE = "ru"
    }
}