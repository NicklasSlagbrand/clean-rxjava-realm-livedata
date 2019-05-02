package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel.Event.CloseScreen
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel.Event.OpenUrl
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel.Event.RenderAttraction
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel.Event.ShowAttractionNotFoundMessage
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.usecase.GetAttractionByIdUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.params.AttractionId
import com.nicklasslagbrand.placeholder.functional.Result
import com.nicklasslagbrand.placeholder.functional.Result.Companion.failure
import com.nicklasslagbrand.placeholder.testMuseumAttraction
import com.nicklasslagbrand.placeholder.testutils.startKoin
import com.nicklasslagbrand.placeholder.testutils.testObserver
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable.just
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class AttractionDetailsViewModelTest : AutoCloseKoinTest() {
    private val useCase = mockk<GetAttractionByIdUseCase>()
    private val viewModel: AttractionDetailsViewModel by inject()

    private var attractionId: Int = 1

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test view does nothing when clicking web link without attraction`() {
        val eventsObserver = viewModel.eventsLiveData.testObserver()
        val failuresObserver = viewModel.failure.testObserver()

        viewModel.onAttractionLinkClicked()

        failuresObserver.shouldBeEmpty()
        eventsObserver.shouldBeEmpty()
    }

    @Test
    fun `test view models handles errors correctly`() {
        val error = Error.GeneralError(NullPointerException())
        every {
            useCase.call(AttractionId(attractionId))
        } answers {
            just(failure(error))
        }
        val failuresObserver = viewModel.failure.testObserver()

        viewModel.initialize(attractionId)

        failuresObserver.shouldContainEvents(
            error
        )
    }

    @Test
    fun `test view models sends message and close events when attraction by id not found`() {
        val attractionId = testMuseumAttraction.id

        every {
            useCase.call(AttractionId(attractionId))
        } answers {
            just(failure(Error.GeneralError(NothingInCache())))
        }
        val eventsObserver = viewModel.eventsLiveData.testObserver()

        viewModel.initialize(attractionId)

        eventsObserver.shouldContainEvents(
            ShowAttractionNotFoundMessage(attractionId.toString()),
            CloseScreen
        )
    }

    @Test
    fun `test view models send correct attraction`() {
        every {
            useCase.call(AttractionId(testMuseumAttraction.id))
        } answers {
            just(Result.success(testMuseumAttraction))
        }
        val eventsObserver = viewModel.eventsLiveData.testObserver()

        viewModel.initialize(testMuseumAttraction.id)

        eventsObserver.shouldContainEvents(
            RenderAttraction(testMuseumAttraction)
        )
    }

    @Test
    fun `test view models send correct url to open`() {
        val eventsObserver = viewModel.eventsLiveData.testObserver()

        viewModel.setAttraction(testMuseumAttraction)
        viewModel.onAttractionLinkClicked()

        eventsObserver.shouldContainEvents(
            OpenUrl(testMuseumAttraction.contactInfo.web)
        )
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { useCase }
        })
    }
}
