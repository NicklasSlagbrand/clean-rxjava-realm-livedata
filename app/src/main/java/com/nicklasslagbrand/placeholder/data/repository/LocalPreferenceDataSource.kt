package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable

interface LocalPreferenceDataSource {
    fun isAppLaunchedFirstTime(): Observable<Result<Boolean, Error>>
    fun setLaunched(): Observable<Result<Any, Error>>
    fun hasUserGaveConsent(): Observable<Result<Boolean, Error>>
    fun isInstructionsShownUseCase(): Observable<Result<Boolean, Error>>
    fun setUserConsent(agreed: Boolean): Observable<Result<Any, Error>>
    fun setIntroShown(isShown: Boolean): Observable<Result<Any, Error>>

    class PreferenceDataSource(
        private val preferenceRepository: LocalPreferenceRepository
    ) : LocalPreferenceDataSource {
        override fun setIntroShown(isShown: Boolean): Observable<Result<Any, Error>> {
            return preferenceRepository.setIntroShown(isShown)
                .toResult()
        }

        override fun hasUserGaveConsent(): Observable<Result<Boolean, Error>> {
            return preferenceRepository.hasUserGaveConsent()
                .toResult()
        }
        override fun isInstructionsShownUseCase(): Observable<Result<Boolean, Error>> {
            return preferenceRepository.isInstructionsShown()
                .toResult()
        }

        override fun setUserConsent(agreed: Boolean): Observable<Result<Any, Error>> {
            return preferenceRepository.setUserConsent(agreed)
                .toResult()
        }

        override fun setLaunched(): Observable<Result<Any, Error>> {
            return preferenceRepository.setLaunched()
                .toResult()
        }

        override fun isAppLaunchedFirstTime(): Observable<Result<Boolean, Error>> {
            return preferenceRepository.isAppLaunchedFirstTime()
                .toResult()
        }
    }
}
