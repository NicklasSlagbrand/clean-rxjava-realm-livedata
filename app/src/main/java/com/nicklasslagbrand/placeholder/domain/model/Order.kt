package com.nicklasslagbrand.placeholder.domain.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("orderReference")
    val orderReference: String,
    @SerializedName("orderStatus")
    val orderStatus: OrderStatus,
    @SerializedName("cardsQty")
    val cardsQty: Int,
    @SerializedName("cards")
    val cards: List<PurchasedCard>
)

enum class OrderStatus {
    @SerializedName("completed")
    Completed,
    @SerializedName("pending")
    Pending
}
