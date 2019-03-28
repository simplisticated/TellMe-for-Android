package com.visuality.tellme.bridge

import androidx.fragment.app.Fragment
import com.visuality.tellme.engine.Speaker
import java.util.*

fun Fragment.tellMeIn(
    language: Locale
) = Speaker(
    this.requireContext(),
    language
)
