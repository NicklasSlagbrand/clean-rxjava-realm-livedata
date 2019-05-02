package com.nicklasslagbrand.placeholder.data.network.body

import com.google.gson.annotations.SerializedName

data class TransferCardBody(
    @SerializedName("newDeviceId") val newDeviceId: String,
    @SerializedName("oldDeviceId") val oldDeviceId: String
)
