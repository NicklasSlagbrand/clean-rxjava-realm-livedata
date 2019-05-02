package com.nicklasslagbrand.placeholder.data.network.response

import com.google.gson.annotations.SerializedName

data class TransferCardResponse(
    @SerializedName("cardId") val cardId: Int,
    @SerializedName("status") val status: String
)
