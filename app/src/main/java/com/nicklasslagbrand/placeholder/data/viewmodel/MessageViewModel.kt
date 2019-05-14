package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllMessagesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase

class MessageViewModel(
    private val getAllMessagessUseCase: GetAllMessagesUseCase
) : RxBaseViewModel() {
    val eventLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun initialize() {
        eventLiveData.value = ConsumableEvent(Event.ShowLoading)
        fetchMessages()
    }

    private fun fetchMessages() {
        addDisposable {
            getAllMessagessUseCase.call(RxUseCase.None)
                .subscribe { result ->
                    result.fold({
                        handleMessages(it)
                    }, ::handleFailure)
                }
        }
    }

    private fun handleMessages(messages: List<Message>) {
        eventLiveData.value = ConsumableEvent(Event.HideLoading)
        eventLiveData.value = ConsumableEvent(Event.ShowMessage(messages))
    }

    sealed class Event {
        data class ShowMessage(val messages: List<Message>) : Event()
        object ShowLoading : Event()
        object HideLoading : Event()
    }
}
