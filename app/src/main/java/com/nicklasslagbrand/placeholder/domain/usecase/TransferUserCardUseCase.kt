package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class TransferUserCardUseCase(
    private val userDataSource: UserDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<Any, TransferUserCardUseCase.TransferCardParam>(workScheduler, callbackScheduler) {
    override fun raw(params: TransferCardParam): Observable<Result<Any, Error>> {
        params.validate()

        return userDataSource.transferCard(params.cardId, params.receiverDeviceId)
    }

    data class TransferCardParam(val cardId: Int, val receiverDeviceId: String) {
        fun validate() {
            if (cardId == 0) {
                throw IllegalArgumentException("'cardId' should not be zero")
            }

            receiverDeviceId.ifBlank {
                throw IllegalArgumentException("'receiverDeviceId' should not be empty")
            }
        }
    }
}
