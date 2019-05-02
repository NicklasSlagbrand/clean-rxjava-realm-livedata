package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.ConfirmEmailFieldError
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.EmailFieldError
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.NameFieldError
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.NavigateToTermsAndConditions
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.PaymentButtonDisabled
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel.Event.PaymentButtonEnabled
import com.nicklasslagbrand.placeholder.domain.model.UserInfo
import com.nicklasslagbrand.placeholder.extension.empty
import com.nicklasslagbrand.placeholder.testutils.TestObserver
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import com.nicklasslagbrand.placeholder.testutils.third
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class ConfirmViewModelTest : AutoCloseKoinTest() {
    private val viewModel: ConfirmViewModel by inject()
    private val basketViewModel = mockk<BasketViewModel>()

    private lateinit var eventObserver: TestObserver<ConsumableEvent<ConfirmViewModel.Event>>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view model sends payment button enable state when checkbox is checked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)

        eventObserver.observedValues.size.shouldEqualTo(1)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
    }

    @Test
    fun `test view model sends payment button disable state when checkbox is not checked`() {
        viewModel.onTermsAndConditionsCheckChanged(false)

        eventObserver.observedValues.size.shouldEqualTo(1)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonDisabled))
    }

    @Test
    fun `test view model sends name error event when name field is empty on payment clicked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)
        viewModel.onUserNameChange(String.empty())
        viewModel.onUserEmailChange("andriy@valtech.com")
        viewModel.onUserConfirmEmailChange("andriy@valtech.com")

        viewModel.onPaymentButtonClicked()

        eventObserver.observedValues.size.shouldEqualTo(2)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(NameFieldError))
    }

    @Test
    fun `test view model sends email error event when email field is empty on payment clicked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)
        viewModel.onUserNameChange("andriy")
        viewModel.onUserEmailChange("")
        viewModel.onUserConfirmEmailChange("andriy@valtech.com")

        viewModel.onPaymentButtonClicked()

        eventObserver.observedValues.size.shouldEqualTo(3)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(EmailFieldError))
        eventObserver.observedValues.third().shouldEqual(ConsumableEvent(ConfirmEmailFieldError))
    }

    @Test
    fun `test view model sends email error event when email field contains invalid email on payment clicked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)
        viewModel.onUserNameChange("andriy")
        viewModel.onUserEmailChange("andriyvaltech.com")
        viewModel.onUserConfirmEmailChange("andriy@valtech.com")

        viewModel.onPaymentButtonClicked()

        eventObserver.observedValues.size.shouldEqualTo(3)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(EmailFieldError))
        eventObserver.observedValues.third().shouldEqual(ConsumableEvent(ConfirmEmailFieldError))
    }

    @Test
    fun `test view model sends confirm email error event when confirm email field is empty on payment clicked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)
        viewModel.onUserNameChange("andriy")
        viewModel.onUserEmailChange("andriy@valtech.com")
        viewModel.onUserConfirmEmailChange("")

        viewModel.onPaymentButtonClicked()

        eventObserver.observedValues.size.shouldEqualTo(2)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(ConfirmEmailFieldError))
    }

    @Test
    fun `test view model sends confirm email error event when confirm email field does not match email on payment clicked`() {
        viewModel.onTermsAndConditionsCheckChanged(true)
        viewModel.onUserNameChange("andriy")
        viewModel.onUserEmailChange("andriy@valtech.com")
        viewModel.onUserConfirmEmailChange("anton@valtech.com")

        viewModel.onPaymentButtonClicked()

        eventObserver.observedValues.size.shouldEqualTo(2)
        eventObserver.observedValues.first().shouldEqual(ConsumableEvent(PaymentButtonEnabled))
        eventObserver.observedValues.second().shouldEqual(ConsumableEvent(ConfirmEmailFieldError))
    }

    @Test
    fun `test view model sends open web page event when terms and conditions button clicked`() {
        viewModel.onTermsAndConditionsClicked()

        eventObserver.observedValues.size.shouldEqualTo(1)
        eventObserver.observedValues.first()
            .shouldEqual(
                ConsumableEvent(
                    NavigateToTermsAndConditions(Constants.TERMS_AND_CONDITIONS_URL)
                )
            )
    }

    @Test
    fun `test view model passes correct data to BasketViewModel when payment button clicked`() {
        val userName = "andriy"
        val userEmail = "andriy@valtech.com"

        viewModel.onUserNameChange(userName)
        viewModel.onUserEmailChange(userEmail)
        viewModel.onUserConfirmEmailChange(userEmail)
        viewModel.onTermsAndConditionsCheckChanged(true)

        val slot = slot<UserInfo>()
        every {
            basketViewModel.onUserInformationReady(capture(slot))
        } just Runs

        viewModel.onPaymentButtonClicked()

        slot.captured.shouldEqual(UserInfo(userName, userEmail))
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
        })

        viewModel.basketViewModel = basketViewModel
        eventObserver = viewModel.eventsLiveData.testObserver()
    }
}
