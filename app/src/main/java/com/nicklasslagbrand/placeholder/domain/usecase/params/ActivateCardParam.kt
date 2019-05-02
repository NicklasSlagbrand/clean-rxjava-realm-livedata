package com.nicklasslagbrand.placeholder.domain.usecase.params

data class ActivateCardParam(val cardId: Int, val activationDate: String) {
    fun validate() {
        if (cardId == 0) {
            throw IllegalArgumentException("'cardId' should not be zero")
        }

        activationDate.ifBlank {
            throw IllegalArgumentException("'activationDate' should not be empty")
        }
    }
}
