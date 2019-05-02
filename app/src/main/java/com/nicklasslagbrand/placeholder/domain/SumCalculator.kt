package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.domain.model.BasketItem
import com.nicklasslagbrand.placeholder.domain.model.Currency

class SumCalculator {
    fun calculateTotalSumForItem(currency: Currency, basketItem: BasketItem): Int {
        val product = basketItem.product
        val price = product.prices.find { it.currencyId == currency.id }
            ?: throw IllegalArgumentException("Currency ${currency.id} not found in product ${product.title}.")

        return price.price * basketItem.numberOfItems
    }

    fun calculateTotalSumListOfItems(currency: Currency, items: List<BasketItem>): Int {
        return items.sumBy {
            calculateTotalSumForItem(currency, it)
        }
    }
}
