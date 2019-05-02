package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
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

class TransferUserCardUseCaseTest : AutoCloseKoinTest() {
    private val useCase: TransferUserCardUseCase by inject()
    private val dataSource: UserDataSource = mockk(relaxed = true)

    private val testAttractionId = 1

    @Test
    fun `use case fails when empty card id provided`() {
        val func = {
            useCase.raw(TransferUserCardUseCase.TransferCardParam(0, "device_id"))
                .checkAndGet()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'cardId' should not be zero")
    }

    @Test
    fun `use case fails when empty receiver device id provided`() {
        val func = {
            useCase.raw(TransferUserCardUseCase.TransferCardParam(testAttractionId, " "))
                .checkAndGet()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'receiverDeviceId' should not be empty")
    }

    @Test
    fun `use case calls correct methods from data source`() {
        useCase.raw(TransferUserCardUseCase.TransferCardParam(testAttractionId, "device_id"))

        verify { dataSource.transferCard(testAttractionId, "device_id") }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        })
    }
}
