package com.nicklasslagbrand.placeholder.feature.transfer

import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*

class QrCodeScannerActivity : BaseActivity() {
    private lateinit var cardId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qr_code_scanner)

        if (intent == null || !intent.hasExtra(CARD_ID_KEY)) {
            showToast("Missing card id")
            finish()
            return
        }

        cardId = intent.getStringExtra(CARD_ID_KEY)

        initializeToolbar()

        bvQrCodeScanner.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val cornerRadius = resources.getDimensionPixelSize(R.dimen.bar_code_view_corner_radius)
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius.toFloat())
            }
        }
        bvQrCodeScanner.clipToOutline = true

        startScanning()
    }

    private fun startScanning() {
        val formats = arrayListOf(BarcodeFormat.QR_CODE)

        bvQrCodeScanner.decoderFactory = DefaultDecoderFactory(formats)
        bvQrCodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text.isNotBlank()) {
                    FinishTransferCardActivity.startActivity(
                        this@QrCodeScannerActivity,
                        cardId,
                        result.text
                    )
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        bvQrCodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        bvQrCodeScanner.pause()
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

    companion object {
        private const val CARD_ID_KEY = "card_id"

        fun startActivity(context: Context, cardId: String) {
            context.startActivity(
                Intent(context, QrCodeScannerActivity::class.java)
                    .putExtra(CARD_ID_KEY, cardId)
            )
        }
    }
}
