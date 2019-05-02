package com.nicklasslagbrand.placeholder.data.network.body

import com.google.gson.annotations.SerializedName

data class CreateOrderBody(
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("orderName")
    val orderName: String,
    @SerializedName("orderEmail")
    val orderEmail: String,
    @SerializedName("orderDeliveryType")
    val orderDeliveryType: String,
    @SerializedName("orderProducts")
    val orderProducts: List<ProductOrderBody>
)

data class ProductOrderBody(
    @SerializedName("qty")
    val productQty: Int,
    @SerializedName("id")
    val productID: Int
)
