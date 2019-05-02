package com.nicklasslagbrand.placeholder.feature.purchase.basket

sealed class ListItem {
    data class ProductItem(val productId: Int, val title: String, val sum: String) : ListItem()
    data class TotalItem(val sum: String) : ListItem()
}
