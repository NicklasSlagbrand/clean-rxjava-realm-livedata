package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.testAdult24Product
import com.nicklasslagbrand.placeholder.testChild24Product
import org.amshove.kluent.shouldEqual
import org.junit.Test

class BasketItemsMergerTest {
    private val originalItemsList = listOf(
        BasketItem(testAdult24Product, 2),
        BasketItem(testChild24Product, 2)
    )

    @Test
    fun `check new items are added correctly when called multiple times`() {
        val newItem = BasketItem(testAdult24Product, 2)

        var resultItems = mergeBasketItems(originalItemsList, listOf(newItem))
        resultItems = mergeBasketItems(resultItems, listOf(BasketItem(testChild24Product, 2)))

        resultItems.shouldEqual(
            listOf(
                BasketItem(testAdult24Product, 4),
                BasketItem(testChild24Product, 4)
            )
        )
    }

    @Test
    fun `check items are merged correctly when two same products are added`() {
        val newItem = BasketItem(testAdult24Product, 2)

        val resultItems = mergeBasketItems(originalItemsList, listOf(newItem))

        resultItems.shouldEqual(
            listOf(
                BasketItem(testAdult24Product, 4),
                BasketItem(testChild24Product, 2)
            )
        )
    }

    @Test
    fun `check nothing changes when merging with empty list`() {
        val resultItems = mergeBasketItems(originalItemsList, emptyList())

        resultItems.shouldEqual(originalItemsList)
    }
}
