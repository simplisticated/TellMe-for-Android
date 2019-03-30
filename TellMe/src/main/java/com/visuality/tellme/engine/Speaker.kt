package com.visuality.tellme.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
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

    private var textToSpeechInitialized = false

    private var speechQueue = arrayListOf<Speech>()

    private var shouldReleaseTextToSpeechWhenQueueBecomesEmpty = false

    fun releaseWhenFinish(): Speaker {
        this.shouldReleaseTextToSpeechWhenQueueBecomesEmpty = true
        return this
    }

    private var onInitListener: OnInitListener? = null

    fun setOnInitListener(listener: OnInitListener?): Speaker {
        this.onInitListener = listener
        return this
    }

    private var onSpeechListener: OnSpeechListener? = null

    fun setOnSpeechListener(listener: OnSpeechListener?): Speaker {
        this.onSpeechListener = listener
        return this
    }

    private fun onTextToSpeechInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            this.onInitListener?.onFailed()
            return
        }

        this.textToSpeech.language = this.language
        this.textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    val speech = this@Speaker.getSpeechFromQueue(utteranceId!!) ?: return
                    Handler(Looper.getMainLooper()).post {
                        this@Speaker.speechQueue.remove(speech)
                        this@Speaker.onSpeechListener?.onFinishedSaying(speech.text)

                        if (this@Speaker.speechQueue.isEmpty() && this@Speaker.shouldReleaseTextToSpeechWhenQueueBecomesEmpty) {
                            this@Speaker.release()
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    super.onError(utteranceId, errorCode)
                }

                override fun onStart(utteranceId: String?) {
                    val speech = this@Speaker.getSpeechFromQueue(utteranceId!!) ?: return
                    Handler(Looper.getMainLooper()).post {
                        this@Speaker.onSpeechListener?.onStartedSaying(
                            speech.text
                        )
                    }
                }

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    super.onRangeStart(utteranceId, start, end, frame)

                    val speech = this@Speaker.getSpeechFromQueue(utteranceId!!) ?: return
                    val position = SpeechPosition(
                        start = start,
                        length = end - start
                    )
                    Handler(Looper.getMainLooper()).post {
                        this@Speaker.onSpeechListener?.onProgress(
                            speech.text,
                            position
                        )
                    }
                }

                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    super.onStop(utteranceId, interrupted)
                }
            }
        )

        this.textToSpeechInitialized = true
        this.onInitListener?.onSuccess()

        for (speech in this.speechQueue) {
            this.pronounceSpeech(speech)
        }
    }

    private fun createSpeechWithText(text: String): Speech {
        LAST_SPEECH_ID++
        return Speech(
            text,
            LAST_SPEECH_ID
        )
    }

    private fun getSpeechFromQueue(utteranceId: String): Speech? {
        return this.speechQueue.firstOrNull { speech ->
            speech.id.toString().equals(
                other = utteranceId,
                ignoreCase = true
            )
        }
    }

    private fun pronounceSpeech(speech: Speech) {
        this.textToSpeech.speak(
            speech.text,
            TextToSpeech.QUEUE_ADD,
            null,
            "${speech.id}"
        )
    }

    private fun putSpeechOnQueue(speech: Speech, pronounce: Boolean) {
        this.speechQueue.add(speech)

        if (pronounce) {
            this.pronounceSpeech(speech)
        }
    }

    fun say(text: String): Speaker {
        val speech = this.createSpeechWithText(text)
        this.putSpeechOnQueue(
            speech = speech,
            pronounce = this.textToSpeechInitialized
        )
        return this
    }

    fun stop() {
        this.textToSpeech.stop()
        this.speechQueue.clear()
    }

    fun release() {
        this.onInitListener = null
        this.onSpeechListener = null
        this.textToSpeech.apply {
            stop()
            shutdown()
        }
    }

    internal companion object {
        private var LAST_SPEECH_ID = 0
    }

    interface OnInitListener {
        fun onSuccess()
        fun onFailed()
    }

    interface OnSpeechListener {
        fun onStartedSaying(text: String)
        fun onProgress(text: String, position: SpeechPosition)
        fun onFinishedSaying(text: String)
    }
}
