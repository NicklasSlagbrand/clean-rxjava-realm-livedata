package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.testutils.TestObserver
import com.nicklasslagbrand.placeholder.testutils.fifth
import com.nicklasslagbrand.placeholder.testutils.fourth
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.sixth
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.third
import io.mockk.clearAllMocks
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class NavigationMenuViewModelTest : AutoCloseKoinTest() {
    private val viewModel: NavigationMenuViewModel by inject()
    private lateinit var eventObserver: TestObserver<ConsumableEvent<NavigationMenuViewModel.Event>>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Ignore
    @Test
    fun `test view model reacts correctly to view actions`() {
        viewModel.initialize(true)
        viewModel.showInstructionsView()
        viewModel.showMapsView()
        viewModel.showAttractionsView()
        viewModel.showCardsView()
        viewModel.showFavouriteView()
        viewModel.showMenuView()

        eventObserver.observedValues.size.shouldEqualTo(6)

        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowCardsView))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowMapsView))
        eventObserver.observedValues.third().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowAttractionsView))
        eventObserver.observedValues.fourth().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowCardsView))
        eventObserver.observedValues.fifth().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowFavouriteView))
        eventObserver.observedValues.sixth().shouldEqual(ConsumableEvent(NavigationMenuViewModel.Event.ShowMenuView))
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
        })
    }
}
