package com.nicklasslagbrand.placeholder.data.entity

import com.nicklasslagbrand.placeholder.data.entity.converter.RealmOrderConverter
import com.nicklasslagbrand.placeholder.testProcessedOrder
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.junit.Test

class RealmOrderConverterTest {
    @Test
    fun `verify the converter maps data correctly`() {
        val realmOrder = RealmOrderConverter.fromOrder(testProcessedOrder)

        RealmOrderConverter.toOrder(realmOrder).shouldEqual(testProcessedOrder)
    }

    @Test
    fun `verify the converter maps data correctly when empty cards`() {
        val realmOrder = RealmOrderConverter.fromOrder(testProcessedOrder)
        realmOrder.cards = null

        RealmOrderConverter.toOrder(realmOrder).cards.isEmpty().shouldBeTrue()
    }
}
