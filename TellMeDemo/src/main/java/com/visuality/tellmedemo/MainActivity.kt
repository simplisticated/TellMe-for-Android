package com.visuality.tellmedemo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.visuality.tellme.bridge.tellMeIn
import com.visuality.tellme.engine.Speaker
import com.visuality.tellme.engine.SpeechPosition
import java.util.*

class MainActivity : AppCompatActivity() {

    private val speechTextView by lazy {
        this.findViewById<TextView>(R.id.speech_text_view)
    }

    private val sayHelloButton by lazy {
        this.findViewById<Button>(R.id.say_hello_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        this.prepareSayHelloButton()
    }

    private fun prepareSayHelloButton() {
        this.sayHelloButton.setOnClickListener {
            this.tellMeIn(Locale.ENGLISH)
                .setOnInitListener(
                    object : Speaker.OnInitListener {
                        override fun onSuccess() {
                        }

                        override fun onFailed() {
                        }
                    }
                )
                .setOnSpeechListener(
                    object : Speaker.OnSpeechListener {
                        override fun onStartedSaying(text: String) {
                            this@MainActivity.speechTextView.text = ""
                        }

                        override fun onProgress(text: String, position: SpeechPosition) {
                            val speech = text.substring(
                                position.start,
                                position.start + position.length - 1
                            )
                            this@MainActivity.speechTextView.text = speech
                        }

                        override fun onFinishedSaying(text: String) {
                            this@MainActivity.speechTextView.text = ""
                        }
                    }
                )
                .say("Hello! How are you doing?")
                .say("What's up?")
                .say("Tell me something new.")
                .destroyWhenFinish()
        }
    }
}
