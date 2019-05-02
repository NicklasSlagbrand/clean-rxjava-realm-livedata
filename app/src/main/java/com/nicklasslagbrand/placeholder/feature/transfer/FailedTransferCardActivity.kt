package com.nicklasslagbrand.placeholder.feature.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import kotlinx.android.synthetic.main.activity_failed_transfer_card.*

class FailedTransferCardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_failed_transfer_card)

        btnCancelTransfer.setOnClickListener {
            MainActivity.returnToActivity(this)
        }
        btnRetryTransfer.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, FailedTransferCardActivity::class.java))
        }
    }
}
