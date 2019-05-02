package com.nicklasslagbrand.placeholder.feature.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.TransferCardViewModel
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import com.nicklasslagbrand.placeholder.view.SlideToActView
import kotlinx.android.synthetic.main.activity_finish_transfer_card.btnCancelTransfer
import kotlinx.android.synthetic.main.activity_finish_transfer_card.pbTransferProgress
import kotlinx.android.synthetic.main.activity_finish_transfer_card.savSlideToTransfer
import kotlinx.android.synthetic.main.activity_finish_transfer_card.tvActivateCardReceiversId
import org.koin.android.viewmodel.ext.android.viewModel

class FinishTransferCardActivity : BaseActivity() {
    private val viewModel: TransferCardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_finish_transfer_card)

        if (intent == null || !intent.hasExtra(CARD_ID_KEY) || !intent.hasExtra(QR_CODE_TEXT_KEY)) {
            showToast("Missing card id or qr code text")
            finish()
            return
        }

        subscribeToLiveData()

        val cardId = intent.getIntExtra(CARD_ID_KEY, 0)
        val qrCodeText = intent.getStringExtra(QR_CODE_TEXT_KEY)

        viewModel.initialize(cardId, qrCodeText)

        savSlideToTransfer.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                savSlideToTransfer.invisible()
                pbTransferProgress.visible()

                viewModel.transferCard()
            }
        }
        btnCancelTransfer.setOnClickListener {
            MainActivity.returnToActivity(this)
        }
    }

    private fun subscribeToLiveData() {
        observe(viewModel.eventsLiveData) {
            handleEvents(it)
        }
        observe(viewModel.failure, ::handleFailure)
    }

    private fun handleEvents(event: ConsumableEvent<TransferCardViewModel.Event>) {
        event.handleIfNotConsumed {
            when (it) {
                is TransferCardViewModel.Event.CardTransferred -> consume {
                    SuccessTransferCardActivity.startActivity(this)
                }
                is TransferCardViewModel.Event.ShowReceiverDeviceId -> consume {
                    tvActivateCardReceiversId.text = it.deviceId
                }
                is TransferCardViewModel.Event.CardTransferFailed -> consume {
                    FailedTransferCardActivity.startActivity(this)

                    resetSlideButtonState()
                }
            }
        }
    }

    private fun resetSlideButtonState() {
        // Schedule UI update cause it happens too fast which causes some too quick animations
        savSlideToTransfer.postDelayed({
            savSlideToTransfer.resetSlider()
            pbTransferProgress.invisible()
            savSlideToTransfer.visible()
        }, RESET_SLIDE_BUTTON_DELAY_MILLIS)
    }

    companion object {
        private const val RESET_SLIDE_BUTTON_DELAY_MILLIS = 200L
        private const val CARD_ID_KEY = "card_id"
        private const val QR_CODE_TEXT_KEY = "qr_code_text_id"

        fun startActivity(context: Context, cardId: String, qrCodeText: String) {
            context.startActivity(
                Intent(context, FinishTransferCardActivity::class.java)
                    .putExtra(CARD_ID_KEY, cardId)
                    .putExtra(QR_CODE_TEXT_KEY, qrCodeText)
            )
        }
    }
}
