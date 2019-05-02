package com.nicklasslagbrand.placeholder.feature.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import kotlinx.android.synthetic.main.activity_sucess_transfer_card.*

class SuccessTransferCardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sucess_transfer_card)

        btnCloseTransfer.setOnClickListener {
            MainActivity.returnToActivity(this, true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        MainActivity.returnToActivity(this, true)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SuccessTransferCardActivity::class.java))
        }
    }
}
