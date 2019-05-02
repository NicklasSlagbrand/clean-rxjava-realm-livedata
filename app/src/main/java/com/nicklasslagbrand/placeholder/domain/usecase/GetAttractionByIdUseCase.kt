package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAttractionByIdUseCase(
    private val attractionsDataSource: AttractionsDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Attraction, AttractionId>(workScheduler, callbackScheduler) {
    override fun raw(params: AttractionId): Observable<Result<Attraction, Error>> {
        params.validate()

        return attractionsDataSource.getAttractionById(params.attractionId)
    }
}
