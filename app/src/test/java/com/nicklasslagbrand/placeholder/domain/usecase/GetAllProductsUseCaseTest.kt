package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import com.nicklasslagbrand.placeholder.testutils.init
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.successFromFile
import io.mockk.clearAllMocks
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.HttpException

class GetAllProductsUseCaseTest : AutoCloseKoinTest() {
    private lateinit var mockWebServer: MockWebServer

    private val useCase: GetAllProductsUseCase by inject()

    @Test
    fun `use case returns failure on network error`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = useCase.raw(RxUseCase.None)
            .checkAndGet()

        result.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(Error.GeneralError::class)

            (it as Error.GeneralError).exception.shouldBeInstanceOf(HttpException::class)
        }
    }

    @Test
    fun `use case returns correct data when requested`() {
        mockWebServer.enqueue(successFromFile("get-all-products-success.json"))

        val result = useCase.raw(RxUseCase.None)
            .checkAndGet()

        result.fold({
            it.size.shouldBe(4)
            it.first().id.shouldBe(1)
            it.first().title.shouldBeEqualTo("Adult 24 hours")
            it.first().validationTime.shouldBe(24)

            it.first().prices.first().currencyId.shouldBeEqualTo("EUR")
            it.first().prices.first().price.shouldBe(54)
        }, ::failIfError)
    }

    @Before
    fun setUp() {
        clearAllMocks()

        mockWebServer = MockWebServer()
        val mockedHost = mockWebServer.init()

        startKoin(overridesModule = module(override = true) {}, wocoBaseUrl = mockedHost, networkLogging = true)
    }
}
