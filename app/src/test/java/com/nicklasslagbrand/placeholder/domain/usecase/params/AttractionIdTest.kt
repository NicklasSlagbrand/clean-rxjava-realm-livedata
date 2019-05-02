package com.nicklasslagbrand.placeholder.domain.usecase.params

import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class AttractionIdTest {
    @Test
    fun `test validation is working correctly`() {
        val emptyIdFunction = {
            AttractionId(0).validate()
        }
        emptyIdFunction
            .shouldThrow(IllegalArgumentException::class)
            .withMessage("'attractionId' should not be zero")
    }
}
