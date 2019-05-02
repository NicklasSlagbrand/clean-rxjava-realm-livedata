package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.AddItemToFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAttractionByIdUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.IsAttractionIdInFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RemoveItemFromFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId

class AttractionDetailsViewModel(
    private val getAttractionById: GetAttractionByIdUseCase,
    private val isAttractionIdInFavourites: IsAttractionIdInFavouritesUseCase,
    private val addItemToFavourites: AddItemToFavouritesUseCase,
    private val removeItemFromFavourites: RemoveItemFromFavouritesUseCase
) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    private var attraction: Attraction? = null
    private var isAddedToFavourites = false

    fun initialize(attractionId: Int) {
        addDisposable {
            getAttractionById.call(AttractionId(attractionId)).subscribe { result ->
                result.fold({
                    handleAttraction(it)
                }) {
                    handleError(it, attractionId)
                }
            }
        }

        addDisposable {
            isAttractionIdInFavourites.call(AttractionId(attractionId)).subscribe { result ->
                result.fold({
                    handleFavouriteStatus(it)
                }, ::handleFailure)
            }
        }
    }

    private fun handleFavouriteStatus(isInFavourites: Boolean) {
        isAddedToFavourites = isInFavourites
        eventsLiveData.value = ConsumableEvent(Event.RenderFavourite(isInFavourites))
    }

    private fun handleError(it: Error, attractionId: Int) {
        if (it is Error.GeneralError && it.exception is NothingInCache) {
            eventsLiveData.value = ConsumableEvent(
                Event.ShowAttractionNotFoundMessage(
                    attractionId.toString()
                )
            )
            eventsLiveData.value = ConsumableEvent(Event.CloseScreen)
        } else {
            failure.value = ConsumableEvent(it)
        }
    }

    fun onAttractionLinkClicked() {
        attraction?.let {
            eventsLiveData.value = ConsumableEvent(Event.OpenUrl(it.contactInfo.web))
        }
    }

    fun onAddRemoveFavouriteClicked() {
        attraction?.let {
            if (isAddedToFavourites) {
                addDisposable {
                    removeItemFromFavourites.call(AttractionId(it.id)).subscribe {
                        it.fold({
                            isAddedToFavourites = false
                            eventsLiveData.value = ConsumableEvent(Event.RenderFavourite(false))
                        }, ::handleFailure)
                    }
                }
            } else {
                addDisposable {
                    addItemToFavourites.call(AttractionId(it.id)).subscribe {
                        it.fold({
                            isAddedToFavourites = true
                            eventsLiveData.value = ConsumableEvent(Event.RenderFavourite(true))
                        }, ::handleFailure)
                    }
                }
            }
        }
    }

    @VisibleForTesting
    fun setAttraction(testAttraction: Attraction) {
        attraction = testAttraction
    }

    private fun handleAttraction(receivedAttraction: Attraction) {
        attraction = receivedAttraction
        eventsLiveData.value = ConsumableEvent(Event.RenderAttraction(receivedAttraction))
    }

    sealed class Event {
        data class RenderFavourite(val isInFavourites: Boolean) : Event()
        data class RenderAttraction(val attraction: Attraction) : Event()
        data class OpenUrl(val url: String) : Event()
        data class ShowAttractionNotFoundMessage(val attractionId: String) : Event()
        object CloseScreen : Event()
    }
}
