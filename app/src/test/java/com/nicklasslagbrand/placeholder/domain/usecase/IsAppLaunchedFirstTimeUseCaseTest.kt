package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceDataSource
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class IsAppLaunchedFirstTimeUseCaseTest : AutoCloseKoinTest() {
    private val useCase: IsAppLaunchedFirstTimeUseCase by inject()
    private val dataSource: LocalPreferenceDataSource = mockk(relaxed = true)

    @Test
    fun `use case calls correct methods from data source`() {
        useCase.raw(RxUseCase.None)

        verify { dataSource.isAppLaunchedFirstTime() }
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { dataSource }
        }, networkLogging = true)
    }
}
