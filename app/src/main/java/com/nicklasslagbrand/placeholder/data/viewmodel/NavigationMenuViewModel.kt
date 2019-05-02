package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowAttractionsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowCardsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowFavouriteView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowMapsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowMenuView
import com.nicklasslagbrand.placeholder.domain.usecase.IsInstructionsShownUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase

class NavigationMenuViewModel(
    private val isInstructionsShownUseCase: IsInstructionsShownUseCase
) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun initialize(shouldGoToCards: Boolean) {
        eventsLiveData.value = if (shouldGoToCards) {
            ConsumableEvent(ShowCardsView)
        } else {
            ConsumableEvent(ShowMapsView)
        }
    }

    fun showInstructionsView() {
        addDisposable {
            isInstructionsShownUseCase.call(RxUseCase.None).subscribe { result ->
                result.fold({ isInstructionsShown ->
                    if (!isInstructionsShown) {
                        eventsLiveData.value = ConsumableEvent(Event.ShowInstructionsView)
                    }
                }, ::doNothing)
            }
        }
    }

    fun showMapsView() {
        eventsLiveData.value = ConsumableEvent(ShowMapsView)
    }

    fun showAttractionsView() {
        eventsLiveData.value = ConsumableEvent(ShowAttractionsView)
    }

    fun showCardsView() {
        eventsLiveData.value = ConsumableEvent(ShowCardsView)
    }

    fun showFavouriteView() {
        eventsLiveData.value = ConsumableEvent(ShowFavouriteView)
    }

    fun showMenuView() {
        eventsLiveData.value = ConsumableEvent(ShowMenuView)
    }

    fun onBackToScreen(shouldGoToCards: Boolean) {
        if (shouldGoToCards) {
            eventsLiveData.value = ConsumableEvent(ShowCardsView)
        }
    }

    sealed class Event {
        object ShowMapsView : Event()
        object ShowAttractionsView : Event()
        object ShowCardsView : Event()
        object ShowFavouriteView : Event()
        object ShowMenuView : Event()
        object ShowInstructionsView : Event()
    }
}
