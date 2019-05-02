package com.nicklasslagbrand.placeholder.domain.datasource

import com.nicklasslagbrand.placeholder.TEST_DEVICE_ID
import com.nicklasslagbrand.placeholder.data.repository.LocalUserRepository
import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.model.CardsContainer
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.testPendingCard
import com.nicklasslagbrand.placeholder.testPendingOrder
import com.nicklasslagbrand.placeholder.testProcessedOrder
import com.nicklasslagbrand.placeholder.testPurchasedChild72ActiveCard
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.doNothingForSuccess
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import com.nicklasslagbrand.placeholder.testutils.fifth
import com.nicklasslagbrand.placeholder.testutils.fourth
import com.nicklasslagbrand.placeholder.testutils.init
import com.nicklasslagbrand.placeholder.testutils.second
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.successFromFile
import com.nicklasslagbrand.placeholder.testutils.third
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.HttpException

class UserDataSourceTest : AutoCloseKoinTest() {
    private val mockWebServer = MockWebServer()
    private val localUserRepository = mockk<LocalUserRepository>()
    private val dataSource: UserDataSource by inject()

    private val testCardId = 1
    private val testActivationDate = "2019-01-28T10:28:37+00:00"

    private val qrCodeUrl = "https://www.dropbox.com/s/xdwrod9uvj75s0e/qrcodeUrl.png?raw=1"

    @Test
    fun `test get user cards return failure if unhandled error happen in local storage`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        every {
            localUserRepository.getAllUserOrders()
        } returns Observable.error(NullPointerException())

        val result =
            dataSource.getAllUserCards()
                .checkAndGet()

        result.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(Error.GeneralError::class)

            (it as Error.GeneralError).exception.shouldBeInstanceOf(NullPointerException::class)
        }
    }

    @Test
    fun `test get user cards return cards from internet if it is available`() {
        mockWebServer.enqueue(successFromFile("get-all-user-cards-success.json"))

        val slot = slot<List<Order>>()
        every {
            localUserRepository.storeOrders(capture(slot))
        } answers { Observable.just(slot.captured) }
        every {
            localUserRepository.getAllUserOrders()
        } answers { Observable.just(emptyList()) }

        val result =
            dataSource.getAllUserCards()
                .checkAndGet()

        result.fold({
            validateCardsAreCorrect(it.currentCards)
        }, ::failIfError)

        slot.captured.size.shouldEqual(2)

        checkGetUserOrdersHttpParams()
    }

    @Test
    fun `test get user cards return cached cards if internet is broken`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        every {
            localUserRepository.getAllUserOrders()
        } returns Observable.just(listOf(testProcessedOrder, testPendingOrder))

        val result =
            dataSource.getAllUserCards()
                .checkAndGet()

        result.fold({
            it.shouldEqual(CardsContainer(listOf(testPurchasedChild72ActiveCard, testPendingCard)))
        }, ::failIfError)
    }

    @Test
    fun `test get user cards returns empty cards list if cached is empty and network is unavailable`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        every {
            localUserRepository.getAllUserOrders()
        } answers { Observable.just(emptyList()) }

        val result =
            dataSource.getAllUserCards()
                .checkAndGet()

        result.fold({
            it.currentCards.isEmpty().shouldBeTrue()
        }, ::failIfError)
    }

    @Test
    fun `test activate user card runs successfully`() {
        mockWebServer.enqueue(successFromFile("get-csrf-token-success.json"))
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val result =
            dataSource.activateCard(testCardId, testActivationDate)
                .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)

        checkActivateCardsHttpParams()
    }

    @Test
    fun `test activate user card fails when network issue happens`() {
        mockWebServer.enqueue(successFromFile("get-csrf-token-success.json"))
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result =
            dataSource.activateCard(testCardId, testActivationDate)
                .checkAndGet()

        result.fold(::doNothingForSuccess) {
            it.shouldBeInstanceOf(Error.GeneralError::class)

            (it as Error.GeneralError).exception.shouldBeInstanceOf(HttpException::class)
        }
    }

    @Test
    fun `test activate user card fails if get token request fails`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result =
            dataSource.activateCard(testCardId, testActivationDate)
                .checkAndGet()

        result.fold(::doNothingForSuccess) {
            it.shouldBeInstanceOf(Error.GeneralError::class)

            (it as Error.GeneralError).exception.shouldBeInstanceOf(HttpException::class)
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

    private fun checkGetUserOrdersHttpParams() {
        mockWebServer.takeRequest().apply {
            path.shouldBeEqualTo("/api/cards?=en%2C$TEST_DEVICE_ID%2Cjson")
        }
    }

    private fun checkActivateCardsHttpParams() {
        mockWebServer.requestCount.shouldBe(2)

        // Skip first token request
        mockWebServer.takeRequest().apply {
            path.shouldBeEqualTo("/rest/session/token")
        }

        mockWebServer.takeRequest().apply {
            path.shouldBeEqualTo("/api/cards/$testCardId/activate")
            body.readByteString().utf8()
                .shouldBeEqualTo("""{"deviceId":"$TEST_DEVICE_ID","activationDate":"$testActivationDate"}""")
        }
    }

    @SuppressWarnings("LongMethod")
    private fun validateCardsAreCorrect(cards: List<PurchasedCard>) {
        cards.first().id.shouldEqual(1234)
        cards.first().validationTime.shouldEqual(24)
        cards.first().qrCodeUrl.shouldEqual(qrCodeUrl)
        cards.first().status.shouldEqual(QrCodeStatus.Expired)
        cards.first().expirationDate.shouldEqual("2018-12-19T14:10:37+0000")

        cards.second().id.shouldEqual(1456)
        cards.second().validationTime.shouldEqual(48)
        cards.second().qrCodeUrl.shouldEqual(qrCodeUrl)
        cards.second().status.shouldEqual(QrCodeStatus.Transferred)
        cards.second().expirationDate.shouldEqual("2019-01-14T14:10:37+0000")

        cards.third().id.shouldEqual(1567)
        cards.third().validationTime.shouldEqual(72)
        cards.third().qrCodeUrl.shouldEqual(qrCodeUrl)
        cards.third().status.shouldEqual(QrCodeStatus.Active)
        cards.third().expirationDate.shouldEqual("2019-02-07T14:10:37+0000")

        cards.fourth().id.shouldEqual(1560)
        cards.fourth().validationTime.shouldEqual(72)
        cards.fourth().qrCodeUrl.shouldEqual(qrCodeUrl)
        cards.fourth().status.shouldEqual(QrCodeStatus.Active)
        cards.fourth().expirationDate.shouldEqual("2019-02-09T14:10:37+0000")

        cards.fifth().id.shouldEqual(1568)
        cards.fifth().validationTime.shouldEqual(48)
        cards.fifth().qrCodeUrl.shouldEqual(qrCodeUrl)
        cards.fifth().status.shouldEqual(QrCodeStatus.NotActivated)
        cards.fifth().expirationDate.shouldBeEmpty()
    }
}
