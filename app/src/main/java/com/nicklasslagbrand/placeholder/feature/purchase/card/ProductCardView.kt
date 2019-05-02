package com.nicklasslagbrand.placeholder.feature.purchase.card

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicklasslagbrand.placeholder.R
import kotlinx.android.synthetic.main.view_product_card.view.*

class ProductCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.view_product_card, this)

        select()
    }

    fun setProductHours(hoursText: String) {
        tvCardHoursValue.text = hoursText
    }

    fun select() {
        if (isSelected) return

        ivCardBackground.setImageResource(R.drawable.placeholder_435x249)

        tvCardHoursValue.setTextColor(Color.WHITE)
        tvCardHoursTitle.setTextColor(Color.WHITE)

        isSelected = true
    }

    fun unSelect() {
        if (!isSelected) return

        ivCardBackground.setImageResource(R.drawable.placeholder_435x249_black)

        tvCardHoursValue.setTextColor(Color.BLACK)
        tvCardHoursTitle.setTextColor(Color.BLACK)

        isSelected = false
    }
}
