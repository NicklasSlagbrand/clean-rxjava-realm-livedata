package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class IsAttractionIdInFavouritesUseCase(
    private val favouritesDataSource: FavouritesDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Boolean, AttractionId>(workScheduler, callbackScheduler) {
    override fun raw(params: AttractionId): Observable<Result<Boolean, Error>> {
        params.validate()

        return favouritesDataSource.isAttractionInFavourites(params.attractionId)
    }
}
