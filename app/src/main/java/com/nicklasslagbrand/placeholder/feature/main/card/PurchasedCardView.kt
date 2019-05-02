package com.nicklasslagbrand.placeholder.feature.main.card

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.extension.invisible
import kotlinx.android.synthetic.main.view_purchased_card.view.*

class PurchasedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.view_purchased_card, this)
    }

    fun updateTexts(validationTime: Int) {
        setProductHours(validationTime.toString())
    }

    fun activate() {
        tvCardHoursValue.setTextColor(Color.WHITE)
        tvCardHoursTitle.setTextColor(Color.WHITE)
        tvCardTypeTitle.setTextColor(Color.WHITE)
    }

    fun deactivate() {
        tvCardHoursValue.setTextColor(Color.BLACK)
        tvCardHoursTitle.setTextColor(Color.BLACK)
        tvCardTypeTitle.setTextColor(Color.BLACK)
    }

    fun setProductHours(hoursText: String) {
        tvCardHoursValue.text = hoursText
    }

    fun hideHoursText() {
        tvCardHoursTitle.invisible()
    }
}
