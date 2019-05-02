package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.model.Price
import com.nicklasslagbrand.placeholder.domain.model.Product
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class SumCalculatorTest {
    private val testPrices = listOf(Price("DKK", 1), Price("USD", 2))
    private val testProduct = Product(1, "adult 24", 24, testPrices)

    private val sumCalculator = SumCalculator()

    @Test
    fun `total sum for single item calculated correctly`() {
        sumCalculator.calculateTotalSumForItem(Currency.DKK, BasketItem(testProduct, 5))
            .shouldBe(5)
    }

    @Test
    fun `total sum for single item calculated correctly for zero items`() {
        sumCalculator.calculateTotalSumForItem(Currency.DKK, BasketItem(testProduct, 0))
            .shouldBe(0)
    }

    @Test
    fun `total sum for single item when currency is missing should throw exception`() {
        val anotherProduct = testProduct.copy(prices = listOf(Price("USD", 20)))

        val throwingFunction = {
            sumCalculator.calculateTotalSumForItem(Currency.DKK, BasketItem(anotherProduct, 5))
        }

        throwingFunction
            .shouldThrow(IllegalArgumentException::class)
            .withMessage("Currency DKK not found in product adult 24.")
    }

    @Test
    fun `total sum for multiple products calculated correctly`() {
        val items = listOf(
            BasketItem(testProduct, 1),
            BasketItem(testProduct, 2),
            BasketItem(testProduct, 3)
        )

        sumCalculator.calculateTotalSumListOfItems(Currency.DKK, items)
            .shouldBe(6)
    }

    @Test
    fun `total sum for empty products should return zero`() {
        sumCalculator.calculateTotalSumListOfItems(Currency.DKK, emptyList())
            .shouldBe(0)
    }
}
