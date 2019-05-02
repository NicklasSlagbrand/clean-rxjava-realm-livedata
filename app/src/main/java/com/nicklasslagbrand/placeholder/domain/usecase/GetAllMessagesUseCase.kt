package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.MessagesDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllMessagesUseCase(
    private val messagesDataSource: MessagesDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<List<Message>, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<List<Message>, Error>> {
        return messagesDataSource.getAllMessages()
    }
}
