package com.nicklasslagbrand.placeholder.testutils

import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.TEST_DEVICE_ID
import com.nicklasslagbrand.placeholder.data.di.generalAppModule
import com.nicklasslagbrand.placeholder.data.di.useCaseAndViewModelModule
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import org.koin.core.Koin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.log.EmptyLogger
import org.koin.standalone.StandAloneContext

fun startKoin(
    overridesModule: Module,
    wocoBaseUrl: String = Constants.API_BASE_URL,
    networkLogging: Boolean = false
): Koin {
    val testScheduler = TestScheduler()

    // Gather all required dependencies
    val allModules = listOf(
        generalAppModule(wocoBaseUrl, networkLogging),
        module {
            single(Constants.DEVICE_ID_TAG) { TEST_DEVICE_ID }
            single(Constants.WORK_SCHEDULER_TAG) { testScheduler as Scheduler }
            single(Constants.CALLBACK_SCHEDULER_TAG) { testScheduler as Scheduler }
        },
        useCaseAndViewModelModule(),
        overridesModule
    )

    return StandAloneContext.startKoin(
        list = allModules,
        logger = EmptyLogger()
    )
}
