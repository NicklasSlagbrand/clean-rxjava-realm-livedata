package com.nicklasslagbrand.placeholder.extension

import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard

fun List<Order>.findDifference(fromStorage: List<Order>): List<PurchasedCard> {
    val allFirstCards = this.map {
        it.cards
    }.flatten()
    val allSecondCards = fromStorage.map {
        it.cards
    }.flatten()

    val diff = mutableListOf<PurchasedCard>()

    allFirstCards.map {
        if (!allSecondCards.contains(it)) {
            diff.add(it)
        }
    }

    return diff
}
