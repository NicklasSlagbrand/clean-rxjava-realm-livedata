package com.nicklasslagbrand.placeholder.data.repository

import io.reactivex.Observable
import io.reactivex.Observable.fromCallable

interface LocalPreferenceRepository {
    fun isAppLaunchedFirstTime(): Observable<Boolean>
    fun setLaunched(): Observable<Any>
    fun hasUserGaveConsent(): Observable<Boolean>
    fun isInstructionsShown(): Observable<Boolean>
    fun setUserConsent(agreed: Boolean): Observable<Any>
    fun setIntroShown(shown: Boolean): Observable<Any>

    class PreferenceRepository(private val storage: PreferenceStorage) : LocalPreferenceRepository {
        override fun setIntroShown(shown: Boolean): Observable<Any> {
            return fromCallable {
                storage.hasIntroBeenShown = shown
                return@fromCallable Any()
            }
        }

        override fun hasUserGaveConsent(): Observable<Boolean> {
            return fromCallable {
                return@fromCallable storage.hasUserGaveConsent
            }
        }
        override fun isInstructionsShown(): Observable<Boolean> {
            return fromCallable {
                return@fromCallable storage.isInstructionsShown
            }
        }

        override fun setUserConsent(agreed: Boolean): Observable<Any> {
            return fromCallable {
                storage.hasUserGaveConsent = agreed
                return@fromCallable Any()
            }
        }

        override fun setLaunched(): Observable<Any> {
            return fromCallable {
                storage.isAppLaunchedFirstTime = false
                return@fromCallable Any()
            }
        }

        override fun isAppLaunchedFirstTime(): Observable<Boolean> {
            return fromCallable {
                return@fromCallable storage.isAppLaunchedFirstTime
            }
        }
    }
}
