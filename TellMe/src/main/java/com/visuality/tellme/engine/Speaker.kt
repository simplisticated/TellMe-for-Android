package com.visuality.tellme.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class Speaker(
    private val context: Context,
    private val language: Locale
) {

    private var textToSpeech = TextToSpeech(
        this.context,
        TextToSpeech.OnInitListener { status ->
            this.onTextToSpeechInit(status)
        }
    )

    private var speechQueue = arrayListOf<Speech>()

    private var lastSpeechId = 0

    private var onReadyHandler: OnSpeakerReadyHandler? = null

    fun whenReady(handler: OnSpeakerReadyHandler): Speaker {
        this.onReadyHandler = handler
        return this
    }

    private var onProgressHandler: OnSpeakerProgressHandler? = null

    fun onProgress(handler: OnSpeakerProgressHandler): Speaker {
        this.onProgressHandler = handler
        return this
    }

    private var onFinishedHandler: OnSpeakerFinishedHandler? = null

    fun onFinished(handler: OnSpeakerFinishedHandler): Speaker {
        this.onFinishedHandler = handler
        return this
    }

    private fun onTextToSpeechInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            return
        }

        this.textToSpeech.language = this.language
        this.textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    val indexToRemove = this@Speaker.speechQueue.indexOfFirst { speech ->
                        speech.id.toString().equals(utteranceId!!, true)
                    }

                    if (0 <= indexToRemove && indexToRemove < this@Speaker.speechQueue.size) {
                        this@Speaker.speechQueue.removeAt(indexToRemove)
                    }

                    this@Speaker.onFinishedHandler?.invoke(this@Speaker)
                }

                override fun onError(utteranceId: String?) {
                }

                override fun onStart(utteranceId: String?) {
                }

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    super.onRangeStart(utteranceId, start, end, frame)
                    this@Speaker.onProgressHandler?.invoke(this@Speaker)
                }
            }
        )
        this.onReadyHandler?.invoke(this)
    }

    private fun putTextOnQueue(text: String) {
        this.lastSpeechId++
        val speech = Speech(
            text,
            this.lastSpeechId
        )
        this.speechQueue.add(speech)
        this.textToSpeech.speak(
            speech.text,
            TextToSpeech.QUEUE_ADD,
            null,
            "${speech.id}"
        )
    }

    fun say(text: String) {
        this.putTextOnQueue(text)
    }

    fun sayImmediately(text: String) {
        this.textToSpeech.stop()
        this.speechQueue.clear()
        this.putTextOnQueue(text)
    }

    fun close() {
        this.onReadyHandler = null
        this.onProgressHandler = null
        this.onFinishedHandler = null
        this.textToSpeech.stop()
        this.textToSpeech.shutdown()
    }
}

typealias OnSpeakerReadyHandler = (speaker: Speaker) -> Unit

typealias OnSpeakerProgressHandler = (speaker: Speaker) -> Unit

typealias OnSpeakerFinishedHandler = (speaker: Speaker) -> Unit
