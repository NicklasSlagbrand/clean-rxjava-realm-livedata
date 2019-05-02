package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.usecase.params.ActivateCardParam
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

class ActivateUserCardUseCaseTest : AutoCloseKoinTest() {
    private val useCase: ActivateUserCardUseCase by inject()
    private val dataSource: UserDataSource = mockk(relaxed = true)

    private val testCardId = 1

    @Test
    fun `use case fails when empty card id provided`() {
        val func = {
            useCase.raw(ActivateCardParam(0, "date"))
                .checkAndGet()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'cardId' should not be zero")
    }

    @Test
    fun `use case fails when empty activation date provided`() {
        val func = {
            useCase.raw(ActivateCardParam(testCardId, " "))
                .checkAndGet()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'activationDate' should not be empty")
    }

    @Test
    fun `use case calls correct methods from data source`() {
        useCase.raw(ActivateCardParam(testCardId, "date"))

        verify { dataSource.activateCard(testCardId, "date") }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        })
    }
}
