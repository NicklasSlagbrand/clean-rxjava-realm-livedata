package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import com.nicklasslagbrand.placeholder.functional.Result.Companion.success
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import org.amshove.kluent.shouldEqual
import org.junit.Test

class RxUseCaseTest {
    @Test
    fun `use case call completes with success`() {
        val testScheduler = TestScheduler()
        val observer = TestUseCase(testScheduler, testScheduler)
            .call(RxUseCase.None)
            .test()

        // Trigger all scheduled actions.
        testScheduler.triggerActions()

        val resultValue = observer.await()
            .assertValueCount(1)
            .assertNoErrors()
            .assertComplete()
            .values()
            .first()

        resultValue.shouldEqual(success(1))
    }

    @Test
    fun `use case raw observable completes with success`() {
        val resultValue = TestUseCase(TestScheduler(), TestScheduler())
            .raw(RxUseCase.None)
            .test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertComplete()
            .values()[0]

        resultValue.shouldEqual(success(1))
    }

    class TestUseCase(workScheduler: Scheduler, callbackScheduler: Scheduler) :
        RxUseCase<Int, RxUseCase.None>(workScheduler, callbackScheduler) {
        override fun raw(params: None): Observable<Result<Int, Error>> {
            return just(success(1))
        }
    }
}
