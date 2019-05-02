package com.nicklasslagbrand.placeholder.domain.model

import com.google.gson.annotations.SerializedName

data class PurchasedCard(
    val id: Int,
    val validationTime: Int,
    val qrCodeUrl: String,
    val status: QrCodeStatus,
    val expirationDate: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PurchasedCard

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + validationTime
        result = 31 * result + qrCodeUrl.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + expirationDate.hashCode()
        return result
    }
}

enum class QrCodeStatus {
    @SerializedName("expired")
    Expired,
    @SerializedName("valid")
    Active,
    @SerializedName("inactive")
    NotActivated,
    @SerializedName("transferred")
    Transferred,
    @SerializedName("pending")
    Pending
}
