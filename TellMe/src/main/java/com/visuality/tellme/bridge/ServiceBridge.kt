package com.visuality.tellme.bridge

import android.app.Service
import com.visuality.tellme.engine.Speaker
import java.util.*

fun Service.tellMeIn(
    language: Locale
) = Speaker(
    this,
    language
)
