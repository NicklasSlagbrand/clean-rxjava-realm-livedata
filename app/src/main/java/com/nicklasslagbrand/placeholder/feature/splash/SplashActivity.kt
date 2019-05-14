package com.nicklasslagbrand.placeholder.feature.splash

import android.os.Bundle
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.SplashViewModel
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.start.StartActivity
import com.nicklasslagbrand.placeholder.view.FirebaseInAppMessagingDisplayImpl
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {
    private val viewModel: SplashViewModel by viewModel()
    private var isMessageSuppressed = false

    override fun onStart() {
        super.onStart()
        overrideInAppMessagingDisplay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        observe(viewModel.completableLiveData) {
            it.handleIfNotConsumed {
                    navigateToMainScreen()
                true
            }
        }
        observe(viewModel.failure, ::handleFailure)

        viewModel.start(isMessageSuppressed)
    }

    private fun navigateToMainScreen() {
        StartActivity.startActivity(this)
        finish()
    }

    private fun overrideInAppMessagingDisplay() {
        val firebaseInAppMessagingDisplay = getInAppMessagingDisplay()

        FirebaseInAppMessaging.getInstance().apply {
            setMessageDisplayComponent(firebaseInAppMessagingDisplay)
        }
    }

    private fun getInAppMessagingDisplay(): FirebaseInAppMessagingDisplay {
        return FirebaseInAppMessagingDisplay { inAppMessage, cb ->
            FirebaseInAppMessagingDisplayImpl(this).let {
                it.setOnDismissDialog(object : OnDialogListener {
                    override fun onDismissDialog() {
                        navigateToMainScreen()
                    }
                })
                it.displayMessage(inAppMessage, cb)
                isMessageSuppressed = true
            }
        }
    }
}
