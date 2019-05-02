package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.BuildConfig

class AppInfoViewModel : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun licenseClick() {
        eventsLiveData.value = ConsumableEvent(Event.NavigateToLicense)
    }

    fun initialize() {
        eventsLiveData.value = ConsumableEvent(Event.AppInfo(buildAppVersionString()))
    }

    private fun buildAppVersionString() = " v.${BuildConfig.VERSION_NAME}"

    sealed class Event {
        object NavigateToLicense : Event()
        data class AppInfo(val version: String) : Event()
    }
}
