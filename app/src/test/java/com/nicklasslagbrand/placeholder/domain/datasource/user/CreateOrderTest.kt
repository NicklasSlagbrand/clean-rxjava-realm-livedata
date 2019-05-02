package com.nicklasslagbrand.placeholder.domain.datasource.user

import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error.GeneralError
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import com.nicklasslagbrand.placeholder.domain.usecase.params.ProductOrder
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import com.nicklasslagbrand.placeholder.testutils.init
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.successFromFile
import io.mockk.clearAllMocks
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.HttpException

class CreateOrderTest : AutoCloseKoinTest() {
    private val mockWebServer = MockWebServer()
    private val dataSource: UserDataSource by inject()

    @Test
    fun `test create order works correctly`() {
        mockWebServer.enqueue(successFromFile("create-order-success.json"))

        val createOrderParam = getTestCreateOrderParams()

        val result =
            dataSource.createOrder(createOrderParam)
                .checkAndGet()

        result.fold({
            it.shouldBeEqualTo("ABC123")
        }, ::failIfError)
    }

    @Test
    fun `test create order returns failure when internet request fails`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val createOrderParam = getTestCreateOrderParams()

        val result =
            dataSource.createOrder(createOrderParam)
                .checkAndGet()

        result.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(GeneralError::class)
            (it as GeneralError).exception.shouldBeInstanceOf(HttpException::class)
        }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        val mockedBaseUrl = mockWebServer.init()
        startKoin(overridesModule = module(override = true) {
        }, wocoBaseUrl = mockedBaseUrl, networkLogging = true)
    }

    private fun getTestCreateOrderParams() = CreateOrderParam(
        orderName = "name",
        orderEmail = "email",
        orderProducts = listOf(
            ProductOrder(2, 1),
            ProductOrder(1, 2)
        )
    )
}
