package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class RemoveItemFromFavouritesUseCase(
    private val favouritesDataSource: FavouritesDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Any, AttractionId>(workScheduler, callbackScheduler) {
    override fun raw(params: AttractionId): Observable<Result<Any, Error>> {
        params.validate()

        return favouritesDataSource.removeFavouriteByAttractionId(params.attractionId)
    }
}
