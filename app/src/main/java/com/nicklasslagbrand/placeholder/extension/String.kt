package com.nicklasslagbrand.placeholder.extension

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String.Companion.empty() = ""

fun String.fromHtml(): Spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
