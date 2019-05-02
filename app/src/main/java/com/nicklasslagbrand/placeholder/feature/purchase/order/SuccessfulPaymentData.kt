package com.nicklasslagbrand.placeholder.feature.purchase.order

data class SuccessfulPaymentData(
    val orderId: String,
    val orderReference: String,
    val amount: String,
    val currency: String,
    val date: String
)
