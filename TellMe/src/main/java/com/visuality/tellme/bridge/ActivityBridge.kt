package com.visuality.tellme.bridge

import android.app.Activity
import com.visuality.tellme.engine.Speaker
import java.util.*

fun Activity.tellMeIn(
    language: Locale
) = Speaker(
    this,
    language
)
