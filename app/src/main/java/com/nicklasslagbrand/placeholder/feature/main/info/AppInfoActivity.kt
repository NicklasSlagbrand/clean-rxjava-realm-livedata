package com.nicklasslagbrand.placeholder.feature.main.info

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat.getColor
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.AppInfoViewModel
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import kotlinx.android.synthetic.main.activity_app_info.*
import org.koin.android.viewmodel.ext.android.viewModel

class AppInfoActivity : BaseActivity() {
    private val viewModel: AppInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        setGradientBackground()

        setupViewModel()
    }

    private fun setupViewModel() {
        observe(viewModel.eventsLiveData) { event ->
            event.handleIfNotConsumed {
                consume {
                    when (it) {
                        is AppInfoViewModel.Event.AppInfo -> {
                            renderAppData(it.version)
                        }
                        is AppInfoViewModel.Event.NavigateToLicense -> {
                            navigateLicensing()
                        }
                    }
                }
            }
        }

        observe(viewModel.failure, ::handleFailure)

        viewModel.initialize()

        btnLicensing.setOnClickListener {
            viewModel.licenseClick()
        }
    }

    private fun navigateLicensing() {
        AppLicensesActivity.startActivity(this)
    }

    private fun setGradientBackground() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                getColor(this, R.color.greenColorOrks),
                getColor(this, R.color.greenColorPlaceholer),
                getColor(this, R.color.greenColorLeaf)
            )
        )

        rootView.background = gradientDrawable
    }

    private fun renderAppData(version: String) {
        tvVersion.append(version)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, AppInfoActivity::class.java))
        }
    }
}
