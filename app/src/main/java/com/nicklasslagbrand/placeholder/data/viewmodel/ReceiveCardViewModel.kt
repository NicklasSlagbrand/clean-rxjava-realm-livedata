package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData

class ReceiveCardViewModel(private val deviceId: String) : RxBaseViewModel() {
    val qrDeviceIdLiveData = MutableLiveData<ConsumableEvent<String>>()

    fun initialize() {
        qrDeviceIdLiveData.value = ConsumableEvent(deviceId)
    }
}
