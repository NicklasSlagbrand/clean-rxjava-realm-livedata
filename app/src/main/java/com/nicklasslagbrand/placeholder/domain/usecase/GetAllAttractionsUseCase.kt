package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllAttractionsUseCase(
    private val attractionsDataSource: AttractionsDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<List<Attraction>, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<List<Attraction>, Error>> {
        return attractionsDataSource.getAllAttractions()
    }
}
