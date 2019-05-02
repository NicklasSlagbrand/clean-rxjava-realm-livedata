package com.nicklasslagbrand.placeholder.extension

import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.testNotActivatedCard
import com.nicklasslagbrand.placeholder.testProcessedOrder
import com.nicklasslagbrand.placeholder.testPurchasedChild72ActiveCard
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainSame
import org.junit.Test

class OrderExtensionsTest {
    @Test
    fun `test extension returns items if new items exists in network response but not in storage`() {
        val processedOrder =
            testProcessedOrder.copy(cards = listOf(testPurchasedChild72ActiveCard, testNotActivatedCard))

        val networkOrders = listOf(processedOrder)
        val storageOrders = listOf(testProcessedOrder)

        val result = networkOrders.findDifference(storageOrders)
        result.shouldContainSame(
            listOf(
                testNotActivatedCard
            )
        )
    }

    @Test
    fun `test extension returns zero if items in network response equal to ones in storage`() {
        val networkOrders = listOf(testProcessedOrder)
        val storageOrders = listOf(testProcessedOrder)

        val result = networkOrders.findDifference(storageOrders)
        result.shouldBeEmpty()
    }

    @Test
    fun `test extension returns zero items if there are more cards in storage rather in network`() {
        val processedOrder =
            testProcessedOrder.copy(cards = listOf(testPurchasedChild72ActiveCard, testNotActivatedCard))

        val storageOrders = listOf(processedOrder)
        val networkOrders = listOf(testProcessedOrder)

        val result = networkOrders.findDifference(storageOrders)
        result.shouldBeEmpty()
    }

    @Test
    fun `test extension returns zero items if both responses contain zero`() {
        val storageOrders = emptyList<Order>()
        val networkOrders = emptyList<Order>()

        val result = networkOrders.findDifference(storageOrders)
        result.shouldBeEmpty()
    }
}
