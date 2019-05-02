package com.nicklasslagbrand.placeholder.feature.main.attraction

import com.nicklasslagbrand.placeholder.domain.model.Attraction

data class SectionsItem(val id: Int, val items: List<FavouriteAttraction>)

data class FavouriteAttraction(val attraction: Attraction, var isFavourite: Boolean)
