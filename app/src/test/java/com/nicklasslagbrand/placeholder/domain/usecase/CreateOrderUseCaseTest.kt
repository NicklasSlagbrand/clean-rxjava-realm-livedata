package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class CreateOrderUseCaseTest : AutoCloseKoinTest() {
    private val useCase: CreateOrderUseCase by inject()
    private val dataSource: UserDataSource = mockk(relaxed = true)

    @Test
    fun `use case calls correct methods from data source`() {
        val mockedParams = mockk<CreateOrderParam>(relaxed = true)
        useCase.raw(mockedParams)

        verify { dataSource.createOrder(mockedParams) }
        verify { mockedParams.validate() }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        })
    }
}
