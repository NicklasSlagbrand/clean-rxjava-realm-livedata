package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.AddItemToFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllFavouriteAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RemoveItemFromFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.feature.main.attraction.FavouriteAttraction
import com.nicklasslagbrand.placeholder.feature.main.attraction.SectionsItem
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.functions.Function3

class AttractionsListViewModel(
    private val getAllAttractions: GetAllAttractionsUseCase,
    private val getAllFavourites: GetAllFavouriteAttractionsUseCase,
    private val removeItemFromFavourites: RemoveItemFromFavouritesUseCase,
    private val addItemToFavouritesUseCase: AddItemToFavouritesUseCase
) : RxBaseViewModel() {
    val sectionsLiveData = MutableLiveData<ConsumableEvent<List<SectionsItem>>>()
    val eventsLiveData = MutableLiveData<ConsumableEvent<FavouriteEvent>>()

    fun fetchSectionsWithAttractions() {
        addDisposable {
            Observable.zip(
                getAllAttractions.call(RxUseCase.None),
                getAllFavourites.call(RxUseCase.None),
                Observable.just(Constants.allCategories),
                Function3<Result<List<Attraction>, Error>, Result<List<Attraction>, Error>,
                    List<AttractionCategory>, Result<List<SectionsItem>, Error>> {
                        result: Result<List<Attraction>, Error>,
                        favouritesResult: Result<List<Attraction>, Error>,
                        categories: List<AttractionCategory> ->
                    when (result) {
                        is Result.Failure -> {
                            return@Function3 Result.failure(result.value)
                        }
                        is Result.Success -> {

                            val favourites: List<Attraction>? = if (favouritesResult is Result.Success) {
                                favouritesResult.value
                            } else {
                                null
                            }
                            val items = createSectionItems(categories, result.value, favourites)

                            return@Function3 Result.success(items!!)
                        }
                    }
                }
            ).subscribe { result ->
                result.fold({
                    handleSectionsWithAttractions(it)
                }, ::handleFailure)
            }
        }
    }

    private fun createSectionItems(
        categories: List<AttractionCategory>,
        attractions: List<Attraction>,
        favourites: List<Attraction>?
    ): List<SectionsItem>? {
        val favouriteAttractions = attractions.map { attraction ->
            FavouriteAttraction(attraction, favourites?.contains(attraction) ?: false)
        }
        return categories
            .map {
                SectionsItem(it.id, findAttractionsByCategory(it.id, favouriteAttractions))
            }
            .filter { !it.items.isNullOrEmpty() }
    }

    private fun findAttractionsByCategory(id: Int, attractions: List<FavouriteAttraction>): List<FavouriteAttraction> {
        return attractions
            .filter { it.attraction.category.id == id }
            .sortedBy { it.attraction.title }
    }

    fun addRemoveToFavorite(favouriteAttraction: FavouriteAttraction) {
        with(favouriteAttraction) {
            if (isFavourite) {
                addToFavorite(attraction)
            } else {
                removeFromFavourite(attraction)
            }
        }
    }

    private fun addToFavorite(attraction: Attraction) {
        addDisposable {
            addItemToFavouritesUseCase.call(AttractionId(attraction.id)).subscribe {
                // in success icons has been updated already just follow failure case
                it.failure {
                    addActionFailure()
                    fetchSectionsWithAttractions()
                }
            }
        }
    }

    fun removeFromFavourite(attraction: Attraction) {
        addDisposable {
            removeItemFromFavourites.call(AttractionId(attraction.id)).subscribe {
                // in success icons has been updated already just follow failure case
                it.failure {
                    removeActionFailure()
                    fetchSectionsWithAttractions()
                }
            }
        }
    }

    private fun addActionFailure() {
        eventsLiveData.value = ConsumableEvent(FavouriteEvent.FailureDuringAddAction)
    }

    private fun removeActionFailure() {
        eventsLiveData.value = ConsumableEvent(FavouriteEvent.FailureDuringRemovalAction)
    }

    private fun handleSectionsWithAttractions(sections: List<SectionsItem>) {
        sectionsLiveData.value = ConsumableEvent(sections)
    }

    sealed class FavouriteEvent {
        object FailureDuringAddAction : FavouriteEvent()
        object FailureDuringRemovalAction : FavouriteEvent()
    }
}
