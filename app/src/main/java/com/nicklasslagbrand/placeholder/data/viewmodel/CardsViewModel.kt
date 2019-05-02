package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.model.CardsContainer
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.domain.usecase.ActivateUserCardUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllUserCardsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.params.ActivateCardParam
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class CardsViewModel(
    private val getAllUserCards: GetAllUserCardsUseCase,
    private val activateUserCardUseCase: ActivateUserCardUseCase
) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()
    val cardsLiveData = MutableLiveData<List<PurchasedCard>>()

    private var cardToActivate: PurchasedCard? = null

    fun initialize() {
        fetchCards()
    }

    private fun fetchCards() {
        eventsLiveData.value = ConsumableEvent(Event.ShowCardsLoading)

        addDisposable {
            getAllUserCards.call(RxUseCase.None)
                .subscribe { result ->
                    result.fold({
                        handleCards(it)
                    }, ::handleFailure)
                }
        }
    }

    private fun handleCards(cards: CardsContainer) {
        if (cards.currentCards.isEmpty()) {
            eventsLiveData.value = ConsumableEvent(Event.ShowNoCards)
        } else {
            cardsLiveData.value = cards.currentCards
        }

        if (cards.isNewCardAvailable) {
            handleNewCard()
        }
    }

    fun activateCardClicked(card: PurchasedCard) {
        cardToActivate = card
        eventsLiveData.value = ConsumableEvent(Event.ShowActivateCardDialog)
    }

    fun retryActivationClicked() {
        eventsLiveData.postValue(ConsumableEvent(Event.ShowActivateCardDialog))
    }

    fun activateCard() {
        cardToActivate?.let {
            addDisposable {
                val currentDateTime = DateTime.now().toString(ISODateTimeFormat.dateTime())

                activateUserCardUseCase.call(
                    ActivateCardParam(
                        it.id,
                        currentDateTime
                    )
                )
                    .subscribe { result ->
                        result.fold({
                            eventsLiveData.postValue(ConsumableEvent(Event.ShowSuccessfulCardActivation))
                            cardToActivate = null
                        }, {
                            eventsLiveData.postValue(ConsumableEvent(Event.ShowFailedCardActivation))
                        })
                    }
            }
        }
    }

    fun onVisibleCardChanged(cardPosition: Int) {
        cardsLiveData.value?.let {
            if (it[cardPosition].status == QrCodeStatus.Active) {
                eventsLiveData.value = ConsumableEvent(Event.SetMaxBrightness)
            } else {
                eventsLiveData.value = ConsumableEvent(Event.SetDefaultBrightness)
            }
        }
    }

    fun onSuccessfulDialogDismissed() {
        fetchCards()
    }

    private fun handleNewCard() {
        eventsLiveData.value = ConsumableEvent(Event.ShowNewCardNotification)
    }

    sealed class Event {
        object ShowNoCards : Event()
        object ShowNewCardNotification : Event()
        object ShowCardsLoading : Event()
        object ShowActivateCardDialog : Event()
        object ShowSuccessfulCardActivation : Event()
        object ShowFailedCardActivation : Event()
        object SetMaxBrightness : Event()
        object SetDefaultBrightness : Event()
    }
}
