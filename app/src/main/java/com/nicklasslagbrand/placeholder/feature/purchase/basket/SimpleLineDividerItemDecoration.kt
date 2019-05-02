package com.nicklasslagbrand.placeholder.feature.purchase.basket

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import com.nicklasslagbrand.placeholder.R

class SimpleLineDividerItemDecoration(context: Context, orientation: Int) :
    DividerItemDecoration(context, orientation) {

    init {
        setDrawable(
            AppCompatResources.getDrawable(context, R.drawable.drawable_line_item_divider)
                ?: throw IllegalArgumentException("Failed to load drawable for item divider.")
        )
    }
}
