package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.domain.model.CardsContainer
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllUserCardsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.functional.Result
import com.nicklasslagbrand.placeholder.testPurchasedAdult24ExpiredCard
import com.nicklasslagbrand.placeholder.testPurchasedChild48NotActivatedCard
import com.nicklasslagbrand.placeholder.testPurchasedChild72ActiveCard
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class CardsViewModelTest : AutoCloseKoinTest() {
    private val viewModel: CardsViewModel by inject()

    private val getUserCards = mockk<GetAllUserCardsUseCase>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view model sends ShowNoCards event when no cards available`() {
        setMockData()
        val testObserver = viewModel.eventsLiveData.testObserver()

        viewModel.initialize()

        testObserver.observedValues.size.shouldEqualTo(2)
        testObserver.observedValues.first().shouldEqual(ConsumableEvent(CardsViewModel.Event.ShowCardsLoading))
        testObserver.observedValues.second().shouldEqual(ConsumableEvent(CardsViewModel.Event.ShowNoCards))
    }

    @Test
    fun `test view model sends correct cards when fetched`() {
        val testCards = arrayOf(
            testPurchasedAdult24ExpiredCard,
            testPurchasedChild72ActiveCard,
            testPurchasedChild48NotActivatedCard
        )
        setMockData(*testCards)
        val testObserver = viewModel.cardsLiveData.testObserver()

        viewModel.initialize()

        testObserver.observedValues.size.shouldEqualTo(1)
        testObserver.observedValues.first().shouldContainSame(testCards)
    }

    private fun setMockData(vararg cards: PurchasedCard) {
        every {
            getUserCards.call(RxUseCase.None)
        } answers {
            Observable.just(Result.success(CardsContainer(cards.toList())))
        }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { getUserCards }
        })
    }
}
