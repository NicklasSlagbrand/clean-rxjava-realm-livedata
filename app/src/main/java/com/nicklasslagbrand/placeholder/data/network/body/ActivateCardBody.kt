package com.nicklasslagbrand.placeholder.data.network.body

import com.google.gson.annotations.SerializedName

data class ActivateCardBody(
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("activationDate") val activationDate: String
)
