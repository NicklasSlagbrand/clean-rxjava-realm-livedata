package com.nicklasslagbrand.placeholder.feature.main.receive

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ReceiveCardViewModel
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import kotlinx.android.synthetic.main.activity_receive_card.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReceiveCardActivity : BaseActivity() {
    private val viewModel: ReceiveCardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_receive_card)

        initViews()

        initializeToolbar()

        setupViewModel()

        viewModel.initialize()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)

        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun generateQRCode(deviceId: String) {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix = multiFormatWriter.encode(deviceId, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

        ivCardBarCode.setImageBitmap(bitmap)
    }

    private fun initViews() {
        btnGoToMyCards.setOnClickListener {
            MainActivity.returnToActivity(this, shouldGoToCards = true)
            finish()
        }
    }

    private fun setupViewModel() {
        observe(viewModel.qrDeviceIdLiveData) {
            it.handleIfNotConsumed { id ->
                tvReceiveAsk2.text = id

                generateQRCode(id)

                true
            }
        }
    }

    companion object {
        const val QR_SIZE: Int = 260

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, ReceiveCardActivity::class.java))
        }
    }
}
