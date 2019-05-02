package com.nicklasslagbrand.placeholder.domain.usecase.params

data class AttractionId(val attractionId: Int) {
    fun validate() {
        if (attractionId == 0) {
            throw IllegalArgumentException("'attractionId' should not be zero")
        }
    }
}
