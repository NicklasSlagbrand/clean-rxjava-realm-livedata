package com.nicklasslagbrand.placeholder.feature.start

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.StartViewModel
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.openUrl
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import com.nicklasslagbrand.placeholder.feature.purchase.PurchaseActivity
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.dialog_consent.*
import org.koin.android.viewmodel.ext.android.viewModel

class StartActivity : BaseActivity() {
    private val viewModel: StartViewModel by viewModel()

    private var consentDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.nicklasslagbrand.placeholder.R.layout.activity_start)

        initViews()

        initViewModel()
    }

    private fun initViewModel() {
        observe(viewModel.eventsLiveData) {
            it.handleIfNotConsumed { event ->
                consume {
                    when (event) {
                        is StartViewModel.Event.ShowConsentView -> {
                            showConsentDialog()
                        }
                        is StartViewModel.Event.DismissConsentDialog -> {
                            consentDialog?.dismiss()
                        }
                    }
                }
            }
        }
        observe(viewModel.failure, ::handleFailure)

        viewModel.initialize()
    }

    private fun initViews() {
        btnBuy.setOnClickListener {
            PurchaseActivity.startActivity(this)
        }

        btnUseWithoutCard.setOnClickListener {
            MainActivity.startActivity(this)
        }
    }

    private fun showConsentDialog() {
        consentDialog?.dismiss()

        consentDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        consentDialog?.let { dialog ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_consent)

            dialog.tvConsentRef.text = Constants.CONSENT_URL
            dialog.tvConsentRef.setOnClickListener {
                openUrl(Constants.CONSENT_URL)
            }
            dialog.btnConsentAgree.setOnClickListener {
                viewModel.onAgreedOnConsent()
            }
            dialog.btnConsentDisagree.setOnClickListener {
                viewModel.onDisagreeOnConsent()
            }

            dialog.show()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, StartActivity::class.java))
        }
    }
}
