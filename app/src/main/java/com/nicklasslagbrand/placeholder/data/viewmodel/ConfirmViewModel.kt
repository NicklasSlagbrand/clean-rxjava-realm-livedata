package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.domain.model.UserInfo
import com.nicklasslagbrand.placeholder.extension.empty

class ConfirmViewModel : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()

    private val emailRegex = Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,8}")

    private var userName = String.empty()
    private var userEmail = String.empty()
    private var userConfirmEmail = String.empty()
    private var termsAccepted = false

    lateinit var basketViewModel: BasketViewModel

    fun onTermsAndConditionsCheckChanged(isChecked: Boolean) {
        termsAccepted = isChecked

        eventsLiveData.value = if (termsAccepted) {
            ConsumableEvent(Event.PaymentButtonEnabled)
        } else {
            ConsumableEvent(Event.PaymentButtonDisabled)
        }
    }

    fun onUserConfirmEmailChange(newUserConfirmEmail: String) {
        userConfirmEmail = newUserConfirmEmail
    }

    fun onUserEmailChange(newUserEmail: String) {
        userEmail = newUserEmail
    }

    fun onUserNameChange(newUserName: String) {
        userName = newUserName
    }

    fun onTermsAndConditionsClicked() {
        eventsLiveData.value = ConsumableEvent(Event.NavigateToTermsAndConditions(Constants.TERMS_AND_CONDITIONS_URL))
    }

    fun onPaymentButtonClicked() {
        var isInputsValid = true

        if (userName.isEmpty()) {
            eventsLiveData.value = ConsumableEvent(Event.NameFieldError)
            isInputsValid = false
        }

        if (!emailRegex.matches(userEmail)) {
            eventsLiveData.value = ConsumableEvent(Event.EmailFieldError)
            isInputsValid = false
        }

        if (userConfirmEmail.isEmpty()) {
            eventsLiveData.value = ConsumableEvent(Event.ConfirmEmailFieldError)
            isInputsValid = false
        } else if (userConfirmEmail != userEmail) {
            eventsLiveData.value = ConsumableEvent(Event.ConfirmEmailFieldError)
            isInputsValid = false
        }

        if (isInputsValid) {
            basketViewModel.onUserInformationReady(UserInfo(userName, userEmail))
        }
    }

    sealed class Event {
        data class NavigateToTermsAndConditions(val url: String) : Event()

        object PaymentButtonEnabled : Event()
        object PaymentButtonDisabled : Event()

        object NameFieldError : Event()
        object EmailFieldError : Event()
        object ConfirmEmailFieldError : Event()
    }
}
