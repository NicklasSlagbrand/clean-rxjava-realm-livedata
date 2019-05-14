package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.usecase.HasUserGaveConsentUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.SetUserConsentUseCase

class StartViewModel(
    private val hasUserGaveConsent: HasUserGaveConsentUseCase,
    private val setUserConsentUseCase: SetUserConsentUseCase
) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun initialize() {
        addDisposable {
            hasUserGaveConsent.call(RxUseCase.None).subscribe { result ->
                result.fold({ hasGave ->
                    if (!hasGave) {
                        eventsLiveData.value = ConsumableEvent(Event.ShowConsentView)
                    }
                }, ::handleFailure)
            }
        }
    }

    fun onAgreedOnConsent() {
        // For now just update the state
        addDisposable { setUserConsentUseCase.call(true).subscribe() }
        eventsLiveData.value = ConsumableEvent(Event.DismissConsentDialog)
    }

    fun onDisagreeOnConsent() {
        // For now just update the state
        addDisposable { setUserConsentUseCase.call(false).subscribe() }
        eventsLiveData.value = ConsumableEvent(Event.DismissConsentDialog)
    }

    sealed class Event {
        object ShowConsentView : Event()
        object DismissConsentDialog : Event()
    }
}
