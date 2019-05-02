package com.nicklasslagbrand.placeholder

import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory

class Constants {
    companion object {
        const val WHATS_INCLUDED_URL = "https://about:blank"
        const val HOW_IT_WORKS_URL = "https://about:blank"
        const val CONDITIONS_URL = "https://about:blank"
        const val DISCOUNTS_URL = "https://about:blank"
        const val TRANSPORT_URL = "https://about:blank"
        const val FAQ_AND_HELP_URL = "https://about:blank"
        const val TERMS_AND_CONDITIONS_URL = "https://about:blank"
        const val CONSENT_URL = "about:blank"

        const val GOOGLE_MAPS_URL = "https://www.google.com/maps/dir/?api=1"

        const val WORK_SCHEDULER_TAG = "work_scheduler"
        const val CALLBACK_SCHEDULER_TAG = "callback_scheduler"
        const val DEVICE_ID_TAG = "device_id"

        const val API_BASE_URL = BuildConfig.API_BASE_URL
        const val BASIC_AUTH_HEADER_VALUE = "Basic dmFsdGVjaDpId3Q2MFplOWFodFIqMlQlazZpTmkqNXU="
        const val APP_LANGUAGE = "en"

        const val CREATE_ORDER_DELIVERY_TYPE = "Mobile ticketing"

        const val SPLASH_SCREEN_DURATION_SECONDS = 2L

        const val APP_CARDS_NOTIFICATION_CHANNEL = "${BuildConfig.APPLICATION_ID}.cards"
        const val NEW_CARD_NOTIFICATION_ID = 1

        val allCategories = listOf(
            AttractionCategory.Art,
            AttractionCategory.Fun,
            AttractionCategory.History,
            AttractionCategory.Royal,
            AttractionCategory.Sight,
            AttractionCategory.Favourite
        )

        const val INSTRUCTION_VIEW_RADIUS = 60
        const val INSTRUCTION_VIEW_TITLE_TEXTSIZE = 20
        const val INSTRUCTION_VIEW_TEXTSIZE = 14
        const val INSTRUCTION_VIEW_OUTERCIRCLE_ALPHA = 0.96f
        const val INSTRUCTION_VIEW_CIRCLE_COLOR = android.R.color.white
        const val INSTRUCTION_VIEW_TEXT_COLOR = android.R.color.white
        const val INSTRUCTION_VIEW_DIM_COLOR = android.R.color.black
    }
}
