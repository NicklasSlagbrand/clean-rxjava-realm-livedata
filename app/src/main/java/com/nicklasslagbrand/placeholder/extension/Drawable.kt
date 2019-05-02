package com.nicklasslagbrand.placeholder.extension

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Drawable.overrideColor(context: Context, @ColorRes colorRes: Int) {
    when (this) {
        is GradientDrawable -> setColor(ContextCompat.getColor(context, colorRes))
        is ShapeDrawable -> paint.color = ContextCompat.getColor(context, colorRes)
        is ColorDrawable -> color = ContextCompat.getColor(context, colorRes)
    }
}
