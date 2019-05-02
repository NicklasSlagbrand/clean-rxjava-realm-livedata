package com.nicklasslagbrand.placeholder.domain.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val title: String,
    val validationTime: Int,
    @SerializedName("price") val prices: List<Price>
)

data class Price(@SerializedName("currencyCode") val currencyId: String, val price: Int)
