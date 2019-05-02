package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.domain.model.Error.GeneralError
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import io.mockk.clearAllMocks
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.AutoCloseKoinTest
import java.util.concurrent.TimeUnit.MILLISECONDS

class RxBaseViewModelTest : AutoCloseKoinTest() {
    private val viewModel = TestViewModel()
    private val testScheduler = TestScheduler()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view model handles failure correctly`() {
        val failureObserver = viewModel.failure.testObserver()
        val testException = Exception()

        viewModel.startWork(testException, testScheduler)
        // Simulate elapsed time
        testScheduler.advanceTimeBy(100, MILLISECONDS)

        failureObserver.observedValues.size.shouldEqualTo(1)
        failureObserver.observedValues.first().shouldEqual(ConsumableEvent(GeneralError(testException)))
    }

    @Test
    fun `test view model disposes subscriptions correctly`() {
        val failureObserver = viewModel.failure.testObserver()
        val testException = Exception()

        viewModel.startWork(testException, testScheduler)
        viewModel.onCleared()

        // Simulate elapsed time
        testScheduler.advanceTimeBy(100, MILLISECONDS)

        failureObserver.observedValues.size.shouldEqualTo(0)
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
        })
    }

    class TestViewModel : RxBaseViewModel() {
        // To simulate concurrency calls in this view model we need to pass test scheduler and simulate some delay
        fun startWork(testException: Exception, testScheduler: TestScheduler) {
            addDisposable {
                Observable.just(testException)
                    .delay(100, MILLISECONDS, testScheduler)
                    .subscribe {
                        handleFailure(GeneralError(it))
                    }
            }
        }
    }
}
