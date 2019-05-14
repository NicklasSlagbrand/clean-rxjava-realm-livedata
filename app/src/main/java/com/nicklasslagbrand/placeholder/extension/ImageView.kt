package com.nicklasslagbrand.placeholder.extension

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImageWithFitCenterTransform(
    url: String,
    vararg transformations: RequestOptions
) =
    Glide.with(context)
        .load(url).apply {
            for (transformation in transformations) {
                apply(transformation)
            }
        }
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)

fun ImageView.loadImageWithFitCenterTransform(
    drawable: Drawable,
    vararg transformations: RequestOptions
) =
    Glide.with(context)
        .load(drawable).apply {
            for (transformation in transformations) {
                apply(transformation)
            }
        }
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)

fun ImageView.loadImageWithCenterCrop(url: String) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions().centerCrop())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}
