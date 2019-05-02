package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.network.PlaceholderApiWrapper
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import io.reactivex.Observable

interface RemoteAttractionsRepository {
    fun getAllAttractions(): Observable<List<Attraction>>

    class NetworkAttractionsRepository(private val placeholderApiWrapper: PlaceholderApiWrapper)
        : RemoteAttractionsRepository {
        override fun getAllAttractions(): Observable<List<Attraction>> =
            placeholderApiWrapper.fetchAllAttractions("full")
    }
}
