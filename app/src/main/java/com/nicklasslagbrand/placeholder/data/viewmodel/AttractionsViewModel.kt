package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllFavouriteAttractionIdsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase

class AttractionsViewModel(
    private val getAllAttractions: GetAllAttractionsUseCase,
    private val getAllFavouriteAttractionIds: GetAllFavouriteAttractionIdsUseCase
) : RxBaseViewModel() {
    val attractionsLiveData = MutableLiveData<ConsumableEvent<List<Attraction>>>()
    val searchResultsLiveData = MutableLiveData<ConsumableEvent<List<Attraction>>>()
    val categoriesLiveData = MutableLiveData<ConsumableEvent<List<AttractionCategory>>>()

    private var allAttractions = emptyList<Attraction>()
    private var allFavouriteAttractionIds = emptyList<Int>()
    private var categoriesSelected = emptyList<AttractionCategory>()

    fun fetchAttractionsAndCategories() {
        fetchCategories()
        fetchAttractions()
        fetchFavouriteIds()
    }

    private fun fetchAttractions() {
        addDisposable {
            getAllAttractions.call(RxUseCase.None)
                .subscribe { result ->
                    result.fold({
                        handleAttractions(it)
                    }, ::handleFailure)
                }
        }
    }
    private fun fetchFavouriteIds() {
        addDisposable {
            getAllFavouriteAttractionIds.call(RxUseCase.None)
                    .subscribe { result ->
                        result.fold({
                            handleFavouriteAttractions(it)
                        }, ::handleFailure)
                    }
        }
    }

    private fun fetchCategories() {
        categoriesLiveData.value = ConsumableEvent(Constants.allCategories)
    }

    fun categoriesSelected(categories: List<AttractionCategory>) {
        categoriesSelected = categories
        fetchFavouriteIds()
        filterAttractionsOnCategories()
    }
    fun updateAttractionsOMap(id: Int) {
        val updatedFavouriteAttractionList = allFavouriteAttractionIds.filter {
            it != id
        }
        handleFavouriteAttractions(updatedFavouriteAttractionList)
        filterAttractionsOnCategories()
    }

    private fun filterAttractionsOnCategories() {
        if (categoriesSelected.isNotEmpty()) {
            val attractions = categoriesSelected.map { category ->
                (if (category.equals(AttractionCategory.Favourite)) {
                    allAttractions.filter { it.id in allFavouriteAttractionIds }
                } else allAttractions.filter { it.category == category })
            }.flatten()

            attractionsLiveData.value = ConsumableEvent(attractions)
        } else {
            attractionsLiveData.value = ConsumableEvent(allAttractions)
        }
    }

    fun filterAttractionsBy(searchCriteria: String) {
        val filteredAttraction = allAttractions.filter {
            it.title.toLowerCase().contains(searchCriteria.toLowerCase())
        }

        searchResultsLiveData.value = ConsumableEvent(filteredAttraction)
    }

    private fun handleAttractions(attractions: List<Attraction>) {
        allAttractions = attractions

        attractionsLiveData.value = ConsumableEvent(attractions)
    }

    private fun handleFavouriteAttractions(favouriteAttractions: List<Int>) {
        allFavouriteAttractionIds = favouriteAttractions
    }
}
