package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.model.Price
import com.nicklasslagbrand.placeholder.domain.model.Product
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllProductsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RxUseCase
import com.nicklasslagbrand.placeholder.feature.purchase.SelectProductCardFragment.PeopleSectionData

class SelectProductViewModel(
    private val getAllProducts: GetAllProductsUseCase
) : RxBaseViewModel() {
    val eventLiveData = MutableLiveData<ConsumableEvent<Event>>()
    val adultCounterLiveData = MutableLiveData<PeopleSectionData>()
    val productCardsLiveData = MutableLiveData<List<Product>>()

    private var numberOfAdults = 0

    private var priceForAdults = 0

    private var currentAdultProduct: Product? = null
    private lateinit var currentCurrency: Currency

    private var allProducts = emptyList<Product>()
    private var allCurrencies = Currency.values().asList()

    lateinit var basketViewModel: BasketViewModel

    fun initialize(deviceLanguage: String) {
        addDisposable {
            getAllProducts.call(RxUseCase.None)
                .subscribe { result ->
                    result.fold({
                        handleProducts(it, deviceLanguage)
                    }, ::handleFailure)
                }
        }
    }

    private fun handleProducts(products: List<Product>, deviceLanguage: String) {
        allProducts = products

        currentCurrency = if (deviceLanguage == "da") {
            allCurrencies.find { it.id == "DKK" } ?: allCurrencies.first()
        } else allCurrencies.first()

        productCardsLiveData.value = allProducts
            .sortedBy { it.validationTime }

        eventLiveData.value = ConsumableEvent(Event.CurrencyChanged(currentCurrency.currencySymbol))

        basketViewModel.setCurrency(currentCurrency)
    }

    fun onProductSelected(productId: Int) {
        currentAdultProduct = allProducts.find { it.id == productId }

        recalculatePeoplePrices()
    }

    fun onAdultAdded() {
        numberOfAdults++

        adultCounterLiveData.value = createCounterValue(numberOfAdults, priceForAdults)
        handleAddButtonState()
    }

    fun onAdultRemoved() {
        numberOfAdults--

        adultCounterLiveData.value = createCounterValue(numberOfAdults, priceForAdults)
        handleAddButtonState()
    }

    fun onCurrencySelected(currency: Currency) {
        currentCurrency = currency

        recalculatePeoplePrices()

        eventLiveData.value = ConsumableEvent(Event.CurrencyChanged(currentCurrency.currencySymbol))

        basketViewModel.setCurrency(currency)
    }

    fun onAddToBasketClicked() {
        val itemsToAdd = mutableListOf<BasketItem>()

        if (numberOfAdults > 0) {
            currentAdultProduct?.let {
                itemsToAdd.add(BasketItem(it, numberOfAdults))
            }
        }

        numberOfAdults = 0

        eventLiveData.value = ConsumableEvent(Event.ClearSelectedCards)
        eventLiveData.value = ConsumableEvent(Event.AddBasketButtonState(false))

        if (itemsToAdd.isNotEmpty()) {
            basketViewModel.addProductsToBasket(itemsToAdd)
        }
    }

    fun onCurrencyChangeRequest() {
        eventLiveData.value = ConsumableEvent(Event.ShowCurrencyDialog(allCurrencies, currentCurrency))
    }

    private fun handleAddButtonState() {
        if (numberOfAdults == 0) {
            eventLiveData.value = ConsumableEvent(Event.AddBasketButtonState(false))
        } else {
            eventLiveData.value = ConsumableEvent(Event.AddBasketButtonState(true))
        }
    }

    private fun recalculatePeoplePrices() {
        currentAdultProduct?.let {
            priceForAdults = findPriceForCurrencyOrUseDefault(it.prices, currentCurrency)
            adultCounterLiveData.value = createCounterValue(numberOfAdults, priceForAdults)
        }
    }

    private fun findPriceForCurrencyOrUseDefault(prices: List<Price>, currency: Currency?): Int {
        val fallbackPrice = prices[0].price

        return if (currency != null) {
            prices.find { it.currencyId == currency.id }?.price ?: fallbackPrice
        } else fallbackPrice
    }

    private fun createCounterValue(numberOfPeople: Int, pricePerPerson: Int): PeopleSectionData {
        // We need to show price for at least one person by default and then increase if required
        val totalSumText = if (numberOfPeople == MINIMAL_NUMBER_OF_PERSONS) {
            calculateAndCreateSumString(1, pricePerPerson, currentCurrency.currencySymbol)
        } else {
            calculateAndCreateSumString(numberOfPeople, pricePerPerson, currentCurrency.currencySymbol)
        }

        val isRemoveEnabled = numberOfPeople != MINIMAL_NUMBER_OF_PERSONS
        val isAddEnabled = numberOfPeople != MAXIMAL_NUMBER_OF_PERSONS

        return PeopleSectionData(numberOfPeople, totalSumText, isRemoveEnabled, isAddEnabled)
    }

    private fun calculateAndCreateSumString(peopleCount: Int, price: Int, currency: String) =
        "${peopleCount * price} $currency"

    sealed class Event {
        data class ShowCurrencyDialog(val currencies: List<Currency>, val currentCurrency: Currency) : Event()
        data class CurrencyChanged(val newCurrencySymbol: String) : Event()
        data class AddBasketButtonState(val isEnabled: Boolean) : Event()
        object ClearSelectedCards : Event()
    }

    companion object {
        const val MINIMAL_NUMBER_OF_PERSONS = 0
        const val MAXIMAL_NUMBER_OF_PERSONS = 99
    }
}
