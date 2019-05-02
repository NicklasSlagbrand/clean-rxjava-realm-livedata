package com.nicklasslagbrand.placeholder.domain.datasource

import android.content.SharedPreferences
import com.nicklasslagbrand.placeholder.data.repository.LocalPreferenceDataSource
import com.nicklasslagbrand.placeholder.data.repository.PreferenceStorage
import com.nicklasslagbrand.placeholder.testutils.checkAndGet
import com.nicklasslagbrand.placeholder.testutils.doNothingForSuccess
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.startKoin
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class PreferencesDataSourceTest : AutoCloseKoinTest() {
    private val editor = mockk<SharedPreferences.Editor>()
    private val sharedPreferences = mockk<SharedPreferences>()
    private val preferenceStorage = PreferenceStorage(sharedPreferences)

    private val dataSource: LocalPreferenceDataSource by inject()

    @Test
    fun `test set intro shown works correctly`() {
        every { sharedPreferences.edit() } answers { editor }
        every {
            editor.putBoolean(PreferenceStorage.PREF_INTRO_SHOWN_KEY, true)
        } answers {
            editor
        }
        every { editor.apply() } just Runs

        val result = dataSource.setIntroShown(true)
            .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)
    }

    @Test
    fun `test set consent agreed works correctly`() {
        every { sharedPreferences.edit() } answers { editor }
        every {
            editor.putBoolean(PreferenceStorage.PREF_USER_CONSENT_KEY, true)
        } answers {
            editor
        }
        every { editor.apply() } just Runs

        val result = dataSource.setUserConsent(true)
            .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)
    }

    @Test
    fun `test get consent returns false by default`() {
        every {
            sharedPreferences.getBoolean(PreferenceStorage.PREF_USER_CONSENT_KEY, false)
        } returns false

        val result = dataSource.hasUserGaveConsent()
            .checkAndGet()

        result.fold({
            it.shouldBeFalse()
        }, ::failIfError)
    }

    @Test
    fun `test get was app launched before returns true`() {
        every {
            sharedPreferences.getBoolean(PreferenceStorage.PREF_APP_LAUNCH_KEY, true)
        } returns true

        val result = dataSource.isAppLaunchedFirstTime()
            .checkAndGet()

        result.fold({
            it.shouldBeTrue()
        }, ::failIfError)
    }

    @Test
    fun `test get was app launched before returns false`() {
        every {
            sharedPreferences.getBoolean(PreferenceStorage.PREF_APP_LAUNCH_KEY, true)
        } returns false

        val result = dataSource.isAppLaunchedFirstTime()
            .checkAndGet()

        result.fold({
            it.shouldBeFalse()
        }, ::failIfError)
    }

    @Test
    fun `test set app launched returns any correctly`() {
        val slot = slot<Boolean>()
        every { sharedPreferences.edit() } answers { editor }
        every {
            editor.putBoolean(PreferenceStorage.PREF_APP_LAUNCH_KEY, capture(slot))
        } answers {
            editor
        }
        every { editor.apply() } just Runs

        val result = dataSource.setLaunched()
            .checkAndGet()

        result.fold(::doNothingForSuccess, ::failIfError)
        slot.captured.shouldBeFalse()
    }

    @Before
    fun setUp() {
        clearAllMocks()

        startKoin(overridesModule = module(override = true) {
            single { preferenceStorage }
        })
    }
}
