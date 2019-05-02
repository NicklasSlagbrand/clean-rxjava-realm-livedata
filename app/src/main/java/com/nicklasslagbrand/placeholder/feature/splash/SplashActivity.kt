package com.nicklasslagbrand.placeholder.feature.splash

import android.os.Bundle
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.SplashViewModel
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.start.StartActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {
    private val viewModel: SplashViewModel by viewModel()

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

        viewModel.start()
    }

    private fun navigateToMainScreen() {
        StartActivity.startActivity(this)
        finish()
    }
}
