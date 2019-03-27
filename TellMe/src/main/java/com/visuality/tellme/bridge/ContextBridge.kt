package com.visuality.tellme.bridge

import android.content.Context
import com.visuality.tellme.engine.Speaker
import java.util.*

fun Context.tellMeIn(
    language: Locale
) = Speaker(
    this,
    language
)
