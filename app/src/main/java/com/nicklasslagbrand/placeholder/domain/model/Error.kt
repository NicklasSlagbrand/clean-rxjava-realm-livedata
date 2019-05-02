package com.nicklasslagbrand.placeholder.domain.model

sealed class Error {
    data class GeneralError(val exception: Throwable) : Error() {
        override fun toString(): String {
            return "GeneralError(exception=$exception)"
        }
    }
}
