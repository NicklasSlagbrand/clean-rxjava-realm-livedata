package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllFavouriteAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RemoveItemFromFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId

class FavouritesViewModel(
    private val getAllFavouriteAttractions: GetAllFavouriteAttractionsUseCase,
    private val removeItemFromFavourites: RemoveItemFromFavouritesUseCase
) : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    fun initialize() {
        fetchFavourites()
    }

    fun onRemoveFavouriteClicked(attraction: Attraction) {
        addDisposable {
            removeItemFromFavourites.call(AttractionId(attraction.id)).subscribe {
                it.fold({
                    fetchFavourites()
                }, ::handleFailure)
            }
        }
    }

    private fun fetchFavourites() {
        addDisposable {
            getAllFavouriteAttractions.call(RxUseCase.None).subscribe { result ->
                result.fold({
                    handleFavourites(it)
                }, ::handleFailure)
            }
        }
    }

    private fun handleFavourites(attractions: List<Attraction>) {
        if (attractions.isEmpty()) {
            eventsLiveData.value = ConsumableEvent(Event.NoFavourites)
        } else {
            eventsLiveData.value = ConsumableEvent(Event.ShowFavouritesList(attractions))
        }
    }

    sealed class Event {
        data class ShowFavouritesList(val favourites: List<Attraction>) : Event()
        object NoFavourites : Event()
    }
}
