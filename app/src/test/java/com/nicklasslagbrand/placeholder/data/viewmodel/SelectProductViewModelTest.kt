package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllProductsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.functional.Result
import com.nicklasslagbrand.placeholder.testAdult24Product
import com.nicklasslagbrand.placeholder.testChild24Product
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Observable
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class SelectProductViewModelTest : AutoCloseKoinTest() {
    private val getAllProductsUseCase = mockk<GetAllProductsUseCase>()

    private val viewModel: SelectProductViewModel by inject()
    private val basketViewModel = mockk<BasketViewModel>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view model sends correct Purchase Steps when requested to move to previous step`() {
        val productsObserver = viewModel.productCardsLiveData.testObserver()
        val eventsObserver = viewModel.eventLiveData.testObserver()

        val slot = slot<Currency>()
        every {
            basketViewModel.setCurrency(capture(slot))
        } just Runs

        viewModel.initialize("ua")

        println("productsObserver = ${productsObserver.observedValues}")
        println("eventsObserver = ${eventsObserver.observedValues}")

        slot.captured.shouldEqual(Currency.EUR)
    }

    @Before
    fun setUp() {
        clearAllMocks()

        every {
            getAllProductsUseCase.call(RxUseCase.None)
        } answers {
            Observable.just(
                Result.success(
                    listOf(testAdult24Product, testChild24Product)
                )
            )
        }

        startKoin(overridesModule = module(override = true) {
            single { getAllProductsUseCase }
        })

        viewModel.basketViewModel = basketViewModel
    }
}
