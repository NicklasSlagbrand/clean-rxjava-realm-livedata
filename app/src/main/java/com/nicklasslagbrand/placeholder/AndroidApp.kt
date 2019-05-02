package com.nicklasslagbrand.placeholder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.nicklasslagbrand.placeholder.data.di.androidPlatformModule
import com.nicklasslagbrand.placeholder.data.di.generalAppModule
import com.nicklasslagbrand.placeholder.data.di.useCaseAndViewModelModule
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import timber.log.Timber
import java.util.UUID

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        JodaTimeAndroid.init(this)

        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .name(packageName + ".realm")
                .deleteRealmIfMigrationNeeded()
                .build()
        )

        startKoin(
            this,
            listOf(
                androidPlatformModule(this, getPseudoUniqueID()),
                generalAppModule(
                    Constants.API_BASE_URL,
                    BuildConfig.DEBUG
                ),
                useCaseAndViewModelModule()
            ),
            logger = AndroidLogger(showDebug = BuildConfig.DEBUG)
        )

        createAppNotificationChannel()
    }

    private fun createAppNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    Constants.APP_CARDS_NOTIFICATION_CHANNEL,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun getPseudoUniqueID(): String {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        //
        // One can find a lot of magic numbers here, but don't worry. Sometimes we need a little magic :)
        val firstMagicNumber = 35
        val secondMagicNumber = 10

        var devIDShort = "$firstMagicNumber"

        devIDShort += Build.BOARD.length % secondMagicNumber
        devIDShort += Build.BRAND.length % secondMagicNumber

        devIDShort += Build.SUPPORTED_ABIS[0].length % secondMagicNumber
        devIDShort += Build.DEVICE.length % secondMagicNumber
        devIDShort += Build.MANUFACTURER.length % secondMagicNumber
        devIDShort += Build.MODEL.length % secondMagicNumber
        devIDShort += Build.PRODUCT.length % secondMagicNumber

        val serial = Build::class.java.getField("SERIAL").get(null).toString()

        // Go ahead and return the serial for api => 9
        return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }
}
