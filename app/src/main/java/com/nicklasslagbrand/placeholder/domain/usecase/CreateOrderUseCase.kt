package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class CreateOrderUseCase(
    private val userDataSource: UserDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<String, CreateOrderParam>(workScheduler, callbackScheduler) {
    override fun raw(params: CreateOrderParam): Observable<Result<String, Error>> {
        params.validate()

        return userDataSource.createOrder(params)
    }
}
