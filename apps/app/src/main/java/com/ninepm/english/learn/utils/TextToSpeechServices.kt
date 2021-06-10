package com.ninepm.english.learn.utils

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class TextToSpeechServices: TextToSpeech.OnInitListener{

    private lateinit var textToSpeech: TextToSpeech

    var sentenceCounter: Int = 0
    var myList: List<String> = ArrayList()

    fun setContext(context: Context) {
        textToSpeech = TextToSpeech(context, this)
    }

    fun resume(text: String) {
        sentenceCounter -= 1
        speakText(text)
    }

    fun stop() {
        sentenceCounter = 0
        textToSpeech.stop()
    }

    fun shutdown() {
        textToSpeech.shutdown()
    }

    fun isSpeak(): Boolean {
        return textToSpeech.isSpeaking
    }

    fun speakText(myText: String) {
        textToSpeech.speak(myText, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = textToSpeech.setLanguage(Locale.US)
            textToSpeech.setSpeechRate(1F)
            textToSpeech.setPitch(1F)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

}

