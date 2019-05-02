package com.nicklasslagbrand.placeholder.feature.main.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import kotlinx.android.synthetic.main.activity_license.*

class AppLicensesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        wvLicense.loadUrl("file:///android_asset/android_license.html")
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, AppLicensesActivity::class.java))
        }
    }
}
