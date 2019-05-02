package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllFavouriteAttractionsUseCase(
    private val favouritesDataSource: FavouritesDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<List<Attraction>, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<List<Attraction>, Error>> {
        return favouritesDataSource.getAttractionsInFavourites()
    }
}
