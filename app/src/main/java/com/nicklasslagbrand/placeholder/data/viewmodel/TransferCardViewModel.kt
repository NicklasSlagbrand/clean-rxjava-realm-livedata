package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.usecase.TransferUserCardUseCase

class TransferCardViewModel(private val transferUserCard: TransferUserCardUseCase) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    private var cardId: Int = 0
    private lateinit var qrCodeText: String

    fun initialize(cardId: Int, qrCodeText: String) {
        eventsLiveData.value = ConsumableEvent(Event.ShowReceiverDeviceId(qrCodeText))

        this.cardId = cardId
        this.qrCodeText = qrCodeText
    }

    fun transferCard() {
        addDisposable {
            transferUserCard.call(TransferUserCardUseCase.TransferCardParam(cardId, qrCodeText))
                .subscribe {
                    it.fold({
                        eventsLiveData.value = ConsumableEvent(Event.CardTransferred)
                    }) {
                        eventsLiveData.value = ConsumableEvent(Event.CardTransferFailed)
                    }
                }
        }
    }

    sealed class Event {
        object CardTransferred : Event()
        object CardTransferFailed : Event()
        data class ShowReceiverDeviceId(val deviceId: String) : Event()
    }
}
