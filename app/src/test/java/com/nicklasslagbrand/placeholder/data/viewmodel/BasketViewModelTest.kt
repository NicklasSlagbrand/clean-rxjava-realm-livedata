package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.model.UserInfo
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem.TotalItem
import com.nicklasslagbrand.placeholder.testAdult24Product
import com.nicklasslagbrand.placeholder.testChild24Product
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import io.mockk.clearAllMocks
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class BasketViewModelTest : AutoCloseKoinTest() {
    private val basketViewModel: BasketViewModel by inject()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view model sends correct Purchase Step when user passes confirm screen`() {
        val stepsObserver = basketViewModel.stepLiveData.testObserver()
        val eventsObserver = basketViewModel.eventsLiveData.testObserver()

        basketViewModel.setCurrency(Currency.DKK)
        basketViewModel.addProductsToBasket(listOf(BasketItem(testChild24Product, 1)))
        basketViewModel.onUserInformationReady(UserInfo("name", "email@valtech.com"))

        eventsObserver.shouldContainEvents(
            BasketViewModel.Event.ShowBasketPopup,
            BasketViewModel.Event.ShowConfirmProgress(true),
            BasketViewModel.Event.ShowConfirmProgress(false)
        )
        stepsObserver.shouldContainValues(
            BasketViewModel.PurchaseStep.Payment
        )
    }

    @Test
    fun `test view model sends correct Purchase Steps when requested to move to previous step`() {
        val stepsObserver = basketViewModel.stepLiveData.testObserver()
        val eventsObserver = basketViewModel.eventsLiveData.testObserver()
        // Lets start from the last step and move backwards
        basketViewModel.setCurrentStep(BasketViewModel.PurchaseStep.Payment)

        basketViewModel.onBackPressed()
        basketViewModel.onBackPressed()
        basketViewModel.onBackPressed()

        stepsObserver.shouldContainValues(
            BasketViewModel.PurchaseStep.Payment,
            BasketViewModel.PurchaseStep.Confirm,
            BasketViewModel.PurchaseStep.Select
        )

        eventsObserver.shouldContainEvents(BasketViewModel.Event.Close)
    }

    @Test
    fun `test view model sends correct Purchase Step when initialized`() {
        val stepsObserver = basketViewModel.stepLiveData.testObserver()

        basketViewModel.initialize()

        stepsObserver.observedValues.size.shouldBe(1)
        stepsObserver.observedValues.first().shouldEqual(BasketViewModel.PurchaseStep.Select)
    }

    @Test
    fun `test view model sends correct Purchase Step when user clicks on confirm button`() {
        val stepsObserver = basketViewModel.stepLiveData.testObserver()

        basketViewModel.onConfirmClicked()

        stepsObserver.observedValues.size.shouldBe(1)
        stepsObserver.observedValues.first().shouldEqual(BasketViewModel.PurchaseStep.Confirm)
    }

    @Test
    fun `test view model sends ShowBasketPopup when user swipes up and basket is not empty`() {
        val eventsObserver = basketViewModel.eventsLiveData.testObserver()

        fulfilBasketWithTestItems()
        eventsObserver.skipPreviousLiveDataEvents()

        basketViewModel.onShowBasketClicked()

        eventsObserver.observedValues.size.shouldBe(1)
        eventsObserver.observedValues.first().shouldEqual(ConsumableEvent(BasketViewModel.Event.ShowBasketPopup))
    }

    @Test
    fun `test view model sends ShowBasketPopup event when item is added to basket`() {
        val eventsObserver = basketViewModel.eventsLiveData.testObserver()

        fulfilBasketWithTestItems()

        eventsObserver.observedValues.size.shouldBe(1)
        eventsObserver.observedValues.first().shouldEqual(ConsumableEvent(BasketViewModel.Event.ShowBasketPopup))
    }

    @Test
    fun `test view model skips ShowBasketPopup event when basket is empty`() {
        val eventsObserver = basketViewModel.eventsLiveData.testObserver()

        basketViewModel.onShowBasketClicked()

        eventsObserver.observedValues.size.shouldBe(0)
    }

    @Test
    fun `test item is remove from basket correctly`() {
        val itemsObserver = basketViewModel.basketItemsLiveData.testObserver()
        val totalObserver = basketViewModel.totalSumLiveData.testObserver()

        basketViewModel.setCurrency(Currency.DKK)
        basketViewModel.addProductsToBasket(
            listOf(
                BasketItem(testAdult24Product, 2),
                BasketItem(testChild24Product, 2)
            )
        )

        itemsObserver.skipPreviousLiveDataEvents()
        totalObserver.skipPreviousLiveDataEvents()

        basketViewModel.onProductRemovedFromBasket(testAdult24Product.id)

        itemsObserver.observedValues.size.shouldBe(1)
        itemsObserver.observedValues.first().shouldEqual(
            listOf(
                ListItem.ProductItem(
                    testChild24Product.id,
                    "Children 24 hours x 2",
                    "2 DKK"
                ),
                ListItem.TotalItem("2 DKK")
            )
        )

        totalObserver.observedValues.size.shouldBe(1)
        totalObserver.observedValues.shouldEqual(
            listOf(true to "2 DKK")
        )
    }

    @Test
    fun `test items are added to basket and proper live data events sent`() {
        val itemsObserver = basketViewModel.basketItemsLiveData.testObserver()
        val totalObserver = basketViewModel.totalSumLiveData.testObserver()

        fulfilBasketWithTestItems()

        itemsObserver.observedValues.size.shouldBe(2)
        itemsObserver.observedValues.first().shouldEqual(
            listOf(TotalItem("0 DKK"))
        )
        itemsObserver.observedValues.second().shouldEqual(
            listOf(
                ListItem.ProductItem(
                    testAdult24Product.id,
                    "Adult 24 hours x 2",
                    "2 DKK"
                ),
                ListItem.TotalItem("2 DKK")
            )
        )

        totalObserver.observedValues.size.shouldBe(3)
        totalObserver.observedValues.shouldEqual(
            listOf(false to "0", false to "0 DKK", true to "2 DKK")
        )
    }

    private fun fulfilBasketWithTestItems() {
        basketViewModel.setCurrency(Currency.DKK)
        basketViewModel.addProductsToBasket(
            listOf(
                BasketItem(testAdult24Product, 2)
            )
        )
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {})
    }
}
