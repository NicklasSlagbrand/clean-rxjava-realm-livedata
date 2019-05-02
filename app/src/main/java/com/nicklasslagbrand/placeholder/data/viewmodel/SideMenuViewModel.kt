package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.Constants

class SideMenuViewModel : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun showWhatsIncluded() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.WHATS_INCLUDED_URL))
    }

    fun showHowItWorks() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.HOW_IT_WORKS_URL))
    }

    fun showConditions() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.CONDITIONS_URL))
    }

    fun showFaqAndHelp() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.FAQ_AND_HELP_URL))
    }

    fun showDiscounts() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.DISCOUNTS_URL))
    }

    fun showTransport() {
        eventsLiveData.value = ConsumableEvent(Event.OpenLinkInExternalBrowser(Constants.TRANSPORT_URL))
    }

    fun showAppInfo() {
        eventsLiveData.value = ConsumableEvent(Event.OpenAppInfoScreen)
    }

    fun showReceiveScreen() {
        eventsLiveData.value = ConsumableEvent(Event.OpenReceiveScreen)
    }

    fun showPurchaseScreen() {
        eventsLiveData.value = ConsumableEvent(Event.OpenPurchaseScreen)
    }

    fun showMessageScreen() {
        eventsLiveData.value = ConsumableEvent(Event.OpenMessageScreen)
    }

    sealed class Event {
        data class OpenLinkInExternalBrowser(val url: String) : Event()
        object OpenAppInfoScreen : Event()
        object OpenReceiveScreen : Event()
        object OpenPurchaseScreen : Event()
        object OpenMessageScreen : Event()
    }
}
