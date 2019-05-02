package com.nicklasslagbrand.placeholder.domain.datasource.user

import com.nicklasslagbrand.placeholder.TEST_DEVICE_ID
import com.nicklasslagbrand.placeholder.data.repository.LocalUserRepository
import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error.GeneralError
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.doNothingForSuccess
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import com.nicklasslagbrand.placeholder.testutils.init
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.successFromFile
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
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

class TransferCardTest : AutoCloseKoinTest() {
    private val mockWebServer = MockWebServer()
    private val dataSource: UserDataSource by inject()

    private val localUserRepository = mockk<LocalUserRepository>()

    private val testCardId = 1

    @Test
    fun `test transfer card works correctly`() {
        mockWebServer.enqueue(successFromFile("get-csrf-token-success.json"))
        mockWebServer.enqueue(successFromFile("transfer-card-success.json"))

        every { localUserRepository.updateCardStatus(testCardId, QrCodeStatus.Transferred) }
            .answers { Observable.just(Any()) }

        val result =
            dataSource.transferCard(testCardId, "device_id")
                .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)

        assertHttpParams()
    }

    @Test
    fun `test transfer card returns failure when get token request fails`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result =
            dataSource.transferCard(testCardId, "device_id")
                .checkAndGet()

        result.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(GeneralError::class)
            (it as GeneralError).exception.shouldBeInstanceOf(HttpException::class)
        }
    }

    @Test
    fun `test transfer card returns failure when internet request fails`() {
        mockWebServer.enqueue(successFromFile("get-csrf-token-success.json"))
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result =
            dataSource.transferCard(testCardId, "device_id")
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
            single { localUserRepository }
        }, wocoBaseUrl = mockedBaseUrl, networkLogging = true)
    }

    private fun assertHttpParams() {
        mockWebServer.requestCount.shouldBe(2)

        // Skip first token request
        mockWebServer.takeRequest().apply {
            path.shouldBeEqualTo("/rest/session/token")
        }

        mockWebServer.takeRequest().apply {
            path.shouldBeEqualTo("/api/cards/$testCardId/transfer?_format=json")
            body.readByteString().utf8()
                .shouldBeEqualTo("""{"newDeviceId":"device_id","oldDeviceId":"$TEST_DEVICE_ID"}""")
        }
    }
}
