package com.nicklasslagbrand.placeholder.data.network.response

import com.google.gson.annotations.SerializedName

data class CreateOrderResponse(
    @SerializedName("orderReference") val orderReference: String,
    @SerializedName("status") val status: String
)
