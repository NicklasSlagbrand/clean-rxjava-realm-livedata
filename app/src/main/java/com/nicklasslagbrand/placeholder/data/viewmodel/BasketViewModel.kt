package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.SumCalculator
import com.nicklasslagbrand.placeholder.domain.mergeBasketItems
import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.model.UserInfo
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem.ProductItem
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem.TotalItem
import com.nicklasslagbrand.placeholder.feature.purchase.order.SuccessfulPaymentData

@SuppressWarnings("TooManyFunctions")
class BasketViewModel(
    private val sumCalculator: SumCalculator
) : RxBaseViewModel() {
    val basketItemsLiveData = MutableLiveData<List<ListItem>>()
    val stepLiveData = MutableLiveData<PurchaseStep>()
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()
    val totalSumLiveData = MutableLiveData<Pair<Boolean, String>>().apply {
        // Will display zero as initial value for total sum and hide switch line
        value = false to "0"
    }

    val paymentSuccessfulLiveData = MutableLiveData<SuccessfulPaymentData>()

    private var currentStep = BasketViewModel.PurchaseStep.Select

    private val items = mutableListOf<BasketItem>()
    private lateinit var currentCurrency: Currency

    fun initialize() {
        setCurrentStep(currentStep)
    }

    fun onShowBasketClicked() {
        if (items.isNotEmpty()) {
            eventsLiveData.value = ConsumableEvent(Event.ShowBasketPopup)
        }
    }

    fun setCurrency(currency: Currency) {
        currentCurrency = currency

        calculateAndUpdateBasketTotalSum()

        basketItemsLiveData.value = convertBasketItemsToListItems(items)
    }

    fun addProductsToBasket(basketItems: List<BasketItem>) {
        checkDuplicatesAndUpdateItems(basketItems)

        calculateAndUpdateBasketTotalSum()

        basketItemsLiveData.value = convertBasketItemsToListItems(items)
        eventsLiveData.value = ConsumableEvent(Event.ShowBasketPopup)
    }

    fun onBackPressed() {
        moveToPreviousStep()
    }

    private fun moveToPreviousStep() {
        when (currentStep) {
            BasketViewModel.PurchaseStep.Select -> {
                if (items.isEmpty()) {
                    eventsLiveData.value = ConsumableEvent(Event.Close)
                } else {
                    eventsLiveData.value = ConsumableEvent(Event.ShowConfirmCloseDialog)
                }
            }
            BasketViewModel.PurchaseStep.Confirm -> {
                setCurrentStep(PurchaseStep.Select)
            }
            BasketViewModel.PurchaseStep.Payment -> {
                setCurrentStep(PurchaseStep.Confirm)
            }
            BasketViewModel.PurchaseStep.Complete -> {
                eventsLiveData.value = ConsumableEvent(Event.Close)
            }
        }
    }

    fun onProductRemovedFromBasket(productId: Int) {
        items.removeAll {
            it.product.id == productId
        }

        calculateAndUpdateBasketTotalSum()

        basketItemsLiveData.value = convertBasketItemsToListItems(items)
    }

    fun onConfirmClicked() {
        setCurrentStep(PurchaseStep.Confirm)
    }

    fun onUserInformationReady(userInfo: UserInfo) {
        eventsLiveData.value = ConsumableEvent(Event.ShowConfirmProgress(true))

        eventsLiveData.value = ConsumableEvent(Event.ShowConfirmProgress(false))
        setCurrentStep(PurchaseStep.Payment)
    }

    fun onMissingDialogCloseClicked() {
        if (currentStep == PurchaseStep.Payment) {
            setCurrentStep(PurchaseStep.Confirm)
        }
    }

    fun onGotToCardsClicked() {
        eventsLiveData.value = ConsumableEvent(Event.NavigateToMyCards)
    }

    fun onCloseScreenClicked() {
        when (currentStep) {
            BasketViewModel.PurchaseStep.Select,
            BasketViewModel.PurchaseStep.Confirm,
            BasketViewModel.PurchaseStep.Payment -> {
                if (items.isEmpty()) {
                    eventsLiveData.value = ConsumableEvent(Event.Close)
                } else {
                    eventsLiveData.value = ConsumableEvent(Event.ShowConfirmCloseDialog)
                }
            }
            BasketViewModel.PurchaseStep.Complete -> {
                eventsLiveData.value = ConsumableEvent(Event.Close)
            }
        }
    }

    @VisibleForTesting
    fun setCurrentStep(step: PurchaseStep) {
        currentStep = step
        stepLiveData.value = currentStep
    }

    private fun checkDuplicatesAndUpdateItems(newItems: List<BasketItem>) {
        val newListOfItems = mergeBasketItems(items, newItems)

        items.clear()
        items.addAll(newListOfItems)
    }

    private fun calculateAndUpdateBasketTotalSum() {
        val totalSum = sumCalculator.calculateTotalSumListOfItems(currentCurrency, items)

        val shouldShowSwitch = items.isNotEmpty()

        totalSumLiveData.value = shouldShowSwitch to currentCurrency.totalSumString(totalSum)
    }

    private fun convertBasketItemsToListItems(basketItems: List<BasketItem>): List<ListItem> {
        var totalBasketSum = 0

        val listItems = basketItems.map {
            val text = "${it.product.title} x ${it.numberOfItems}"
            val totalForProduct =
                sumCalculator.calculateTotalSumForItem(currentCurrency, it)

            totalBasketSum += totalForProduct

            ProductItem(
                it.product.id,
                text,
                currentCurrency.totalSumString(totalForProduct)
            ) as ListItem
        }.toMutableList()

        listItems.add(TotalItem(currentCurrency.totalSumString(totalBasketSum)))

        return listItems
    }

    sealed class Event {
        object Close : Event()
        object ShowConfirmCloseDialog : Event()
        object ShowBasketPopup : Event()
        data class ShowConfirmProgress(val showProgress: Boolean) : Event()
        object ShowNetworkMissingDialog : Event()
        object NavigateToMyCards : Event()
    }

    enum class PurchaseStep {
        Select, Confirm, Payment, Complete
    }
}
