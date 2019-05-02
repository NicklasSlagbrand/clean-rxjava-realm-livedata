package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.domain.model.BasketItem

fun mergeBasketItems(originalItems: List<BasketItem>, newItems: List<BasketItem>): List<BasketItem> {
    val itemsToReturn = originalItems.toMutableList()

    newItems.forEach { newItem ->
        val foundItem = itemsToReturn.find { newItem.product.id == it.product.id }

        if (foundItem != null) {
            // Replace current item in list with new one keeping the same position
            val totalAmount = foundItem.numberOfItems + newItem.numberOfItems

            val currentItemIndex = itemsToReturn.indexOf(foundItem)

            itemsToReturn.removeAt(currentItemIndex)
            itemsToReturn.add(currentItemIndex, newItem.copy(numberOfItems = totalAmount))
        } else {
            itemsToReturn.add(newItem)
        }
    }

    return itemsToReturn
}
