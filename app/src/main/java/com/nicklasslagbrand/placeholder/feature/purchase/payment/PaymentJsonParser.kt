package com.nicklasslagbrand.placeholder.feature.purchase.payment

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nicklasslagbrand.placeholder.data.network.fromJson
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.feature.purchase.order.SuccessfulPaymentData
import com.nicklasslagbrand.placeholder.functional.Result

object PaymentJsonParser {
    fun parseJson(jsonString: String): Result<SuccessfulPaymentData, Error> {
        return try {
            val jsonObject = Gson().fromJson<JsonObject>(jsonString).get("data").asJsonObject

            val orderId = jsonObject.get("orderid").asString
            val reference = jsonObject.get("reference").asString
            val amount = jsonObject.get("amount").asString
            val currency = jsonObject.get("currency").asString
            val date = jsonObject.get("date").asString

            Result.success(SuccessfulPaymentData(orderId, reference, amount, currency, date))
        } catch (exception: IllegalStateException) {
            Result.failure<SuccessfulPaymentData, Error>(Error.GeneralError(exception))
        }
    }
}
