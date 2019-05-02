package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.Constants.Companion.CONDITIONS_URL
import com.nicklasslagbrand.placeholder.Constants.Companion.FAQ_AND_HELP_URL
import com.nicklasslagbrand.placeholder.Constants.Companion.HOW_IT_WORKS_URL
import com.nicklasslagbrand.placeholder.Constants.Companion.WHATS_INCLUDED_URL
import com.nicklasslagbrand.placeholder.data.viewmodel.SideMenuViewModel.Event.OpenLinkInExternalBrowser
import com.nicklasslagbrand.placeholder.testutils.fourth
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import com.nicklasslagbrand.placeholder.testutils.third
import io.mockk.clearAllMocks
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class SideMenuViewModelTest : AutoCloseKoinTest() {
    private val viewModel: SideMenuViewModel by inject()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test side menu view model sends correct events`() {
        val testObserver = viewModel.eventsLiveData.testObserver()

        viewModel.showWhatsIncluded()
        viewModel.showHowItWorks()
        viewModel.showConditions()
        viewModel.showFaqAndHelp()

        testObserver.observedValues.size.shouldEqualTo(4)

        testObserver.observedValues.first().shouldEqual(ConsumableEvent(OpenLinkInExternalBrowser(WHATS_INCLUDED_URL)))
        testObserver.observedValues.second().shouldEqual(ConsumableEvent(OpenLinkInExternalBrowser(HOW_IT_WORKS_URL)))
        testObserver.observedValues.third().shouldEqual(ConsumableEvent(OpenLinkInExternalBrowser(CONDITIONS_URL)))
        testObserver.observedValues.fourth().shouldEqual(ConsumableEvent(OpenLinkInExternalBrowser(FAQ_AND_HELP_URL)))
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
        })
    }
}
