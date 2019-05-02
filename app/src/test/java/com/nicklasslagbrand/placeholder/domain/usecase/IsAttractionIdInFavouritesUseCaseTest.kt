package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class IsAttractionIdInFavouritesUseCaseTest : AutoCloseKoinTest() {
    private val useCase: IsAttractionIdInFavouritesUseCase by inject()
    private val dataSource: FavouritesDataSource = mockk(relaxed = true)

    @Test
    fun `use case calls correct methods from data source`() {
        useCase.raw(AttractionId(1))

        verify { dataSource.isAttractionInFavourites(1) }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        }, networkLogging = true)
    }
}
