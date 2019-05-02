package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class SetAppLaunchedUseCase(
    private val preferenceAppDataSource: LocalPreferenceDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Any, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<Any, Error>> {
        return preferenceAppDataSource.setLaunched()
    }
}
