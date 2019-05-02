package com.nicklasslagbrand.placeholder.domain.model

enum class Currency(val id: String, val currencyName: String, val currencySymbol: String) {
    EUR("EUR", "Euro", "€"),
    USD("USD", "US Dollar", "$"),
    DKK("DKK", "Danish Krone", "DKK"),
    GBP("GBP", "British Pound", "£");

    fun totalSumString(totalSum: Int) = "$totalSum $currencySymbol"
}
