package com.nicklasslagbrand.placeholder.domain.usecase

import androidx.annotation.VisibleForTesting
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

abstract class RxUseCase<Output : Any, in Params : Any>(
    private val workScheduler: Scheduler,
    private val callbackScheduler: Scheduler
) {
    @VisibleForTesting
    abstract fun raw(params: Params): Observable<Result<Output, Error>>

    fun call(params: Params): Observable<Result<Output, Error>> {
        return raw(params)
            .observeOn(callbackScheduler)
            .subscribeOn(workScheduler)
    }

    object None
}
