package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class GetAttractionByIdUseCaseTest : AutoCloseKoinTest() {
    private val useCase: GetAttractionByIdUseCase by inject()
    private val dataSource: AttractionsDataSource = mockk(relaxed = true)

    private val testAttractionId = 1

    @Test
    fun `use case call proper methods of its data source`() {
        useCase.raw(AttractionId(testAttractionId))

        verify { dataSource.getAttractionById(testAttractionId) }
    }

    @Test
    fun `use case fails when empty attraction id provided`() {
        val func = {
            useCase.raw(AttractionId(0))
                .checkAndGet()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'attractionId' should not be zero")
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        }, networkLogging = true)
    }
}
