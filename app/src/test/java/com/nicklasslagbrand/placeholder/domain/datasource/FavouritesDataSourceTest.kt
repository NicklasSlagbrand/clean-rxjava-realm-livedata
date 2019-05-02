package com.nicklasslagbrand.placeholder.domain.datasource

import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalFavouritesRepository
import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Favourite
import com.nicklasslagbrand.placeholder.testMuseumAttraction
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.doNothingForSuccess
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Observable
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class FavouritesDataSourceTest : AutoCloseKoinTest() {
    private val localAttractionsRepository = mockk<LocalAttractionsRepository>()
    private val localFavouritesRepository = mockk<LocalFavouritesRepository>()

    private val dataSource: FavouritesDataSource by inject()

    private var testAttractionId: Int = 1

    @Test
    fun `test favourite is saved correctly`() {
        val slot = slot<Favourite>()
        every {
            localFavouritesRepository.saveFavouriteItem(capture(slot))
        } answers { Observable.just(Any()) }

        val result = dataSource.saveAttractionIdToFavourites(testAttractionId)
            .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)

        slot.captured.attractionId.`should be equal to`(testAttractionId)
    }

    @Test
    fun `test get all favourites returns saved favourites sorted by oldest first`() {
        every {
            localFavouritesRepository.getAllFavourites()
        } answers { Observable.just(testRealmFavourites) }

        val result = dataSource.getAllFavouritesItems()
            .checkAndGet()

        result.fold({
            it.shouldContainSame(
                listOf(
                    Favourite(1, 1),
                    Favourite(2, 2)
                )
            )
        }, ::failIfError)
    }

    @Test
    fun `test remove favourite by attraction id works correctly`() {
        every {
            localFavouritesRepository.removeFavourite(testAttractionId)
        } answers { Observable.just(Any()) }

        val result = dataSource.removeFavouriteByAttractionId(testAttractionId)
            .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)
    }

    @Test
    fun `test is attraction in favourites works correctly if it exists in cache`() {
        every {
            localFavouritesRepository.getFavourite(testAttractionId)
        } answers { Observable.just(Favourite(testAttractionId, 1)) }

        val result = dataSource.isAttractionInFavourites(testAttractionId)
            .checkAndGet()

        result.fold({
            it.shouldBeTrue()
        }, ::failIfError)
    }

    @Test
    fun `test is attraction in favourites returns false if favourite is missing in cache`() {
        every {
            localFavouritesRepository.getFavourite(testAttractionId)
        } answers { Observable.error(NothingInCache()) }

        val result = dataSource.isAttractionInFavourites(testAttractionId)
            .checkAndGet()

        result.fold({
            it.shouldBeFalse()
        }, ::failIfError)
    }

    @Test
    fun `test get attractions in favourites returns correct list of attractions`() {
        every {
            localFavouritesRepository.getAllFavourites()
        } answers { Observable.just(listOf(Favourite(testAttractionId, 1))) }

        every {
            localAttractionsRepository.getAttractionsByIds(listOf(testAttractionId))
        } answers { Observable.just(listOf(testMuseumAttraction)) }

        val result = dataSource.getAttractionsInFavourites()
            .checkAndGet()

        result.fold({
            it.shouldContainSame(listOf(testMuseumAttraction))
        }, ::failIfError)
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { localAttractionsRepository }
            single { localFavouritesRepository }
        })
    }

    private val testRealmFavourites = listOf(
        Favourite(2, 2),
        Favourite(1, 1)
    )
}
