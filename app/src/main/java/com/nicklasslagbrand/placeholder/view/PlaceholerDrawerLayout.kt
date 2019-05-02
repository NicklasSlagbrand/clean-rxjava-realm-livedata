package com.nicklasslagbrand.placeholder.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.nicklasslagbrand.placeholder.R
import kotlinx.android.synthetic.main.view_drawer_content.view.*

@SuppressWarnings("TooManyFunctions")
class PlaceholerDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    lateinit var onMenuItemsClickListener: OnSideMenuItemClickListener

    init {
        inflate(context, R.layout.view_drawer_content, this)

        btnMessage.setOnClickListener {
            onMenuItemsClickListener.onMessageClicked()
        }
        btnBuy.setOnClickListener {
            onMenuItemsClickListener.onBuyCardClicked()
        }
        btnReceiveCard.setOnClickListener {
            onMenuItemsClickListener.onReceiveCardClicked()
        }
        btnAppInfo.setOnClickListener {
            onMenuItemsClickListener.onAppInfoClicked()
        }
        btnReceiveCard.setOnClickListener {
            onMenuItemsClickListener.onReceiveCardClicked()
        }
        btnWhatsIncluded.setOnClickListener {
            onMenuItemsClickListener.onWhatsIncludedClicked()
        }
        btnHowItWorks.setOnClickListener {
            onMenuItemsClickListener.onHowItWorksClicked()
        }
        btnConditions.setOnClickListener {
            onMenuItemsClickListener.onConditionsClicked()
        }
        btnDiscounts.setOnClickListener {
            onMenuItemsClickListener.onDiscountsClicked()
        }
        btnTransport.setOnClickListener {
            onMenuItemsClickListener.onTransportClicked()
        }
        btnFaqAndHelp.setOnClickListener {
            onMenuItemsClickListener.onFaqAndHelpClicked()
        }
    }

    interface OnSideMenuItemClickListener {
        fun onBuyCardClicked()
        fun onReceiveCardClicked()
        fun onAppInfoClicked()
        fun onMessageClicked()

        fun onWhatsIncludedClicked()
        fun onHowItWorksClicked()
        fun onConditionsClicked()
        fun onDiscountsClicked()
        fun onTransportClicked()
        fun onFaqAndHelpClicked()
    }
}
