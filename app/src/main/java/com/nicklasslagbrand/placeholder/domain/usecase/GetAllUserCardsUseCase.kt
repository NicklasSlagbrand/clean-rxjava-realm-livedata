package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.CardsContainer
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllUserCardsUseCase(
    private val userDataSource: UserDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<CardsContainer, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<CardsContainer, Error>> {
        return userDataSource.getAllUserCards()
    }
}
