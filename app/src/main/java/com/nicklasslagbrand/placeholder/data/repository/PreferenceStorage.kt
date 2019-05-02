package com.nicklasslagbrand.placeholder.data.repository

import android.content.SharedPreferences

class PreferenceStorage(private val preferences: SharedPreferences) {

    var hasIntroBeenShown: Boolean
        get() {
            return preferences.getBoolean(PREF_INTRO_SHOWN_KEY, false)
        }
        set(value) {
            preferences.edit().apply {
                putBoolean(PREF_INTRO_SHOWN_KEY, value)

                apply()
            }
        }

    var hasUserGaveConsent: Boolean
        get() {
            return preferences.getBoolean(PREF_USER_CONSENT_KEY, false)
        }
        set(value) {
            preferences.edit().apply {
                putBoolean(PREF_USER_CONSENT_KEY, value)

                apply()
            }
        }
    var isInstructionsShown: Boolean
        get() {
            return preferences.getBoolean(PREF_USER_INSTRUCTIONS_KEY, false)
        }
        set(value) {
            preferences.edit().apply {
                putBoolean(PREF_USER_INSTRUCTIONS_KEY, value)

                apply()
            }
        }

    var isAppLaunchedFirstTime: Boolean
        get() {
            return preferences.getBoolean(PREF_APP_LAUNCH_KEY, true)
        }
        set(value) {
            preferences.edit().apply {
                putBoolean(PREF_APP_LAUNCH_KEY, value)

                apply()
            }
        }

    companion object {
        const val PREF_APP_LAUNCH_KEY = "key_app_was_launched"
        const val PREF_USER_CONSENT_KEY = "key_user_consent"
        const val PREF_USER_INSTRUCTIONS_KEY = "key_user_intructions"
        const val PREF_INTRO_SHOWN_KEY = "key_intro_shown"
    }
}
