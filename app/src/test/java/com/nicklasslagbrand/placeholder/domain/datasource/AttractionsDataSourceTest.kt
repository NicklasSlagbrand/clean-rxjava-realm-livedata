package com.nicklasslagbrand.placeholder.domain.datasource

import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository
import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.model.Error.GeneralError
import com.nicklasslagbrand.placeholder.domain.model.Location
import com.nicklasslagbrand.placeholder.testMuseumAttraction
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import com.nicklasslagbrand.placeholder.testutils.fifth
import com.nicklasslagbrand.placeholder.testutils.init
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.successFromFile
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.HttpException

class AttractionsDataSourceTest : AutoCloseKoinTest() {
    private val mockWebServer = MockWebServer()
    private val localAttractionsRepository = mockk<LocalAttractionsRepository>()
    private val dataSource: AttractionsDataSource by inject()

    @Test
    fun `test get attractions return failure if unhandled error happen in local storage`() {
        every {
            localAttractionsRepository.getAllAttractions()
        } answers { Observable.error(NullPointerException()) }

        val attractionsResult =
            dataSource.getAllAttractions()
                .checkAndGet()

        attractionsResult.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(GeneralError::class)

            (it as GeneralError).exception.shouldBeInstanceOf(NullPointerException::class)
        }
    }

    @Test
    fun `test get attractions return attractions from internet if caches is empty`() {
        mockWebServer.enqueue(successFromFile("get-all-attractions-success.json"))

        val slot = slot<List<Attraction>>()
        every {
            localAttractionsRepository.getAllAttractions()
        } answers { Observable.error(NothingInCache()) }
        every {
            localAttractionsRepository.storeAllAttractions(capture(slot))
        } answers { Observable.just(slot.captured) }

        val attractionsResult =
            dataSource.getAllAttractions()
                .checkAndGet()

        attractionsResult.fold({
            assertAttractions(it)
        }, ::failIfError)
    }

    @Test
    fun `test get attractions return cached attractions if cached is not empty`() {
        every {
            localAttractionsRepository.getAllAttractions()
        } answers { Observable.just(listOf(testMuseumAttraction)) }

        val attractionsResult =
            dataSource.getAllAttractions()
                .checkAndGet()

        attractionsResult.fold({
            it.shouldContainSame(listOf(testMuseumAttraction))
        }, ::failIfError)
    }

    @Test
    fun `test get attractions fails if cached is empty and network is unavailable`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        every {
            localAttractionsRepository.getAllAttractions()
        } returns Observable.error(NothingInCache())

        val attractionsResult =
            dataSource.getAllAttractions()
                .checkAndGet()

        attractionsResult.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(GeneralError::class)

            (it as GeneralError).exception.shouldBeInstanceOf(HttpException::class)
        }
    }

    @Test
    fun `test get attraction by id returns NothingInCache if attraction with provided ID is not found`() {
        every {
            localAttractionsRepository.getAttractionById(testMuseumAttraction.id)
        } returns Observable.error(NothingInCache())

        val attractionsResult =
            dataSource.getAttractionById(testMuseumAttraction.id)
                .checkAndGet()

        attractionsResult.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(GeneralError::class)

            (it as GeneralError).exception.shouldBeInstanceOf(NothingInCache::class)
        }
    }

    @Test
    fun `test get attraction by id returns attraction from cache`() {
        every {
            localAttractionsRepository.getAttractionById(testMuseumAttraction.id)
        } answers { Observable.just(testMuseumAttraction) }

        val attractionsResult =
            dataSource.getAttractionById(testMuseumAttraction.id)
                .checkAndGet()

        attractionsResult.fold({
            it.shouldEqual(testMuseumAttraction)
        }, ::failIfError)
    }

    @Before
    fun setUp() {
        clearAllMocks()

        val mockedBaseUrl = mockWebServer.init()
        startKoin(overridesModule = module(override = true) {
            single { localAttractionsRepository }
        }, wocoBaseUrl = mockedBaseUrl)
    }

    @SuppressWarnings("LongMethod")
    private fun assertAttractions(it: List<Attraction>) {
        it.size.shouldBe(5)

        it.first().id.shouldBe(1)
        it.first().title.shouldBeEqualTo("Tivoli Gardens")
        it.first().discount.shouldBeEqualTo("No discount")
        it.first().category.shouldEqual(AttractionCategory.Fun)
        it.first()
            .teaser.shouldEqual("<p><strong>Museet viser kunstværker af den kontroversielle og banebrydende danske kunstner J.F. Willumsen (1863-1958)</strong></p>\n<p>Oplev Willumsens store farveglæde og sprudlende skabertrang indenfor både maleri, skulptur, grafik, fotografi, keramik og arkitektur samt spektakulære værker fra hans samling af ældre europæisk kunst.</p>")
        it.first()
            .description.shouldEqual("<p><strong>Museet viser kunstværker af den kontroversielle og banebrydende danske kunstner J.F. Willumsen (1863-1958)</strong></p>\n<p>Oplev Willumsens store farveglæde og sprudlende skabertrang indenfor både maleri, skulptur, grafik, fotografi, keramik og arkitektur samt spektakulære værker fra hans samling af ældre europæisk kunst.</p>\n<p>Museet har desuden en VÆRKboks - en interaktiv kasse til udlån. VÆRKboksen kan guide børn til udvalgte værker af Willumsen og i museets underetage kan børnene male og tegne selv.</p>")
        it.first().category.shouldEqual(AttractionCategory.Fun)
        it.first().location.shouldEqual(Location(55.674541, 12.565190))
        it.first().contactInfo.address.shouldBeEqualTo("Jenriksvej 4 , Frederikssund 3600 DK")
        it.first().contactInfo.phone.shouldBeEqualTo("4731 0773")
        it.first().contactInfo.web.shouldBeEqualTo("http://www.jfwillumsensmuseum.dk")
        it.first().images.shouldContainSame(listOf("https://via.placeholder.com/1024x768"))
        it.first().openingHours.size.shouldBe(1)
        it.first().openingHours[0].startDate.shouldBeEqualTo("2019-01-02")
        it.first().openingHours[0].endDate.shouldBeEqualTo("2019-12-22")
        it.first().openingHours[0].days.size.shouldBe(7)
        it.first().openingHours[0].days[0].name.shouldBeEqualTo("monday")
        it.first().openingHours[0].days[0].opening.shouldBeEqualTo("11:00")
        it.first().openingHours[0].days[0].closing.shouldBeEqualTo("23:00")

        it.fifth().id.shouldBe(5)
        it.fifth().title.shouldBeEqualTo("Canal Tours Copenhagen")
        it.fifth().discount.shouldBeEqualTo("No discount")
        it.fifth().category.shouldEqual(AttractionCategory.Sight)
        it.fifth().location.shouldEqual(Location(55.677210, 12.581630))
        it.fifth().openingHours.size.shouldBe(1)
        it.fifth().openingHours[0].days.size.shouldBe(7)
    }
}
