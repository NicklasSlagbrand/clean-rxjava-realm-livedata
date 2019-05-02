package com.nicklasslagbrand.placeholder.data.di

import android.content.Context
import android.preference.PreferenceManager
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.data.network.CommonRequestParams
import com.nicklasslagbrand.placeholder.data.network.PlaceholderApiWrapper
import com.nicklasslagbrand.placeholder.data.network.createPlaceholderApi
import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository.RealmAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalFavouritesRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalFavouritesRepository.RealmLocalFavouritesRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceDataSource
import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceDataSource.PreferenceDataSource
import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceRepository.PreferenceRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalUserRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalUserRepository.RealmLocalUserRepository
import com.nicklasslagbrand.placeholder.data.repository.PreferenceStorage
import com.nicklasslagbrand.placeholder.data.repository.RemoteAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteAttractionsRepository.NetworkAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteMessagesRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteMessagesRepository.NetworkMessageRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteProductsRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteProductsRepository.NetworkProductsRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteUserRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteUserRepository.NetworkUserRepository
import com.nicklasslagbrand.placeholder.data.viewmodel.AppInfoViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionsListViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.CardsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.FavouritesViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.MapViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.MessageViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ReceiveCardViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.SelectProductViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.SideMenuViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.SplashViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.StartViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.TransferCardViewModel
import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.AttractionsDataSource.DefaultAttractionsDataSource
import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.FavouritesDataSource.DefaultFavouritesDataSource
import com.nicklasslagbrand.placeholder.domain.MessagesDataSource
import com.nicklasslagbrand.placeholder.domain.MessagesDataSource.DefaultMessagesDataSource
import com.nicklasslagbrand.placeholder.domain.ProductsDataSource
import com.nicklasslagbrand.placeholder.domain.ProductsDataSource.DefaultProductsDataSource
import com.nicklasslagbrand.placeholder.domain.SumCalculator
import com.nicklasslagbrand.placeholder.domain.UserDataSource
import com.nicklasslagbrand.placeholder.domain.UserDataSource.DefaultUserDataSource
import com.nicklasslagbrand.placeholder.domain.usecase.ActivateUserCardUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.AddItemToFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.CreateOrderUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllFavouriteAttractionIdsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllFavouriteAttractionsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllMessagesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllProductsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAllUserCardsUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.GetAttractionByIdUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.HasUserGaveConsentUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.IsAppLaunchedFirstTimeUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.IsAttractionIdInFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.IsInstructionsShownUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.RemoveItemFromFavouritesUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.SetAppLaunchedUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.SetIntroShownUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.SetUserConsentUseCase
import com.nicklasslagbrand.placeholder.domain.usecase.TransferUserCardUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

fun androidPlatformModule(context: Context, deviceId: String) = module {
    single {
        PreferenceStorage(PreferenceManager.getDefaultSharedPreferences(context))
    }
    single(Constants.DEVICE_ID_TAG) { deviceId }
    single(Constants.WORK_SCHEDULER_TAG) { Schedulers.io() }
    single(Constants.CALLBACK_SCHEDULER_TAG) { AndroidSchedulers.mainThread() }
}

fun generalAppModule(wocoBaseUrl: String, networkLogging: Boolean) =
    module {
        single {
            PlaceholderApiWrapper(
                CommonRequestParams(get(Constants.DEVICE_ID_TAG), Constants.APP_LANGUAGE),
                createPlaceholderApi(
                    debug = networkLogging,
                    baseUrl = wocoBaseUrl
                )
            )
        }
        single { NetworkProductsRepository(get()) as RemoteProductsRepository }
        single { DefaultProductsDataSource(get()) as ProductsDataSource }

        single { NetworkAttractionsRepository(get()) as RemoteAttractionsRepository }
        single { RealmAttractionsRepository() as LocalAttractionsRepository }
        single { DefaultAttractionsDataSource(get(), get()) as AttractionsDataSource }

        single { RealmLocalFavouritesRepository() as LocalFavouritesRepository }
        single { DefaultFavouritesDataSource(get(), get()) as FavouritesDataSource }

        single { PreferenceDataSource(get()) as LocalPreferenceDataSource }
        single { PreferenceRepository(get()) as LocalPreferenceRepository }

        single { RealmLocalUserRepository() as LocalUserRepository }
        single { NetworkUserRepository(get()) as RemoteUserRepository }

        single { DefaultUserDataSource(get(), get()) as UserDataSource }

        single { NetworkMessageRepository(get()) as RemoteMessagesRepository }
        single { DefaultMessagesDataSource(get()) as MessagesDataSource }

        single { SumCalculator() }
    }

fun useCaseAndViewModelModule() = module {
    factory {
        GetAttractionByIdUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllProductsUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllAttractionsUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllUserCardsUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        ActivateUserCardUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllFavouriteAttractionsUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        IsAttractionIdInFavouritesUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        AddItemToFavouritesUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        RemoveItemFromFavouritesUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        IsAppLaunchedFirstTimeUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        SetAppLaunchedUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        CreateOrderUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        TransferUserCardUseCase(
                get(),
                get(Constants.WORK_SCHEDULER_TAG),
                get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllFavouriteAttractionIdsUseCase(
                get(),
                get(Constants.WORK_SCHEDULER_TAG),
                get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        HasUserGaveConsentUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        IsInstructionsShownUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        SetUserConsentUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        SetIntroShownUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    factory {
        GetAllMessagesUseCase(
            get(),
            get(Constants.WORK_SCHEDULER_TAG),
            get(Constants.CALLBACK_SCHEDULER_TAG)
        )
    }
    viewModel { SplashViewModel() }
    viewModel { StartViewModel(get(), get()) }
    viewModel { BasketViewModel(get()) }
    viewModel { SelectProductViewModel(get()) }
    viewModel { ConfirmViewModel() }
    viewModel { NavigationMenuViewModel(get()) }
    viewModel { SideMenuViewModel() }
    viewModel { MapViewModel() }
    viewModel { AttractionsViewModel(get(), get()) }
    viewModel { CardsViewModel(get(), get()) }
    viewModel { AttractionDetailsViewModel(get(), get(), get(), get()) }
    viewModel { FavouritesViewModel(get(), get()) }
    viewModel { AttractionsListViewModel(get(), get(), get(), get()) }
    viewModel { AppInfoViewModel() }
    viewModel { MessageViewModel(get()) }
    viewModel { ReceiveCardViewModel(get(Constants.DEVICE_ID_TAG)) }
    viewModel { TransferCardViewModel(get()) }
}
