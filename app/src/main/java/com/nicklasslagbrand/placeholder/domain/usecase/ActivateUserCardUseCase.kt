package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.params.ActivateCardParam
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class ActivateUserCardUseCase(
    private val userDataSource: UserDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Any, ActivateCardParam>(workScheduler, callbackScheduler) {
    override fun raw(params: ActivateCardParam): Observable<Result<Any, Error>> {
        params.validate()

        return userDataSource.activateCard(params.cardId, params.activationDate)
    }
}
