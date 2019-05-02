package com.nicklasslagbrand.placeholder.feature.purchase

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.SelectProductViewModel
import com.nicklasslagbrand.placeholder.extension.gone
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.isVisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import com.nicklasslagbrand.placeholder.feature.purchase.basket.BasketBottomSheetDialogFragment
import com.nicklasslagbrand.placeholder.feature.purchase.basket.SwipeGestureListener
import com.nicklasslagbrand.placeholder.feature.purchase.confirm.ConfirmFragment
import com.nicklasslagbrand.placeholder.feature.purchase.order.OrderCompleteFragment
import com.nicklasslagbrand.placeholder.feature.purchase.payment.PaymentFragment
import kotlinx.android.synthetic.main.activity_purchase.*
import kotlinx.android.synthetic.main.view_total_sum_bottom.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.Locale

class PurchaseActivity : BaseActivity() {
    private val selectProductViewModel: SelectProductViewModel by viewModel()
    private val basketViewModel: BasketViewModel by viewModel()

    private var networkMissingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_purchase)

        subscribeLiveData()

        intiViews()

        basketViewModel.initialize()

        selectProductViewModel.basketViewModel = basketViewModel
        selectProductViewModel.initialize(Locale.getDefault().language)
    }

    private fun subscribeLiveData() {
        observe(selectProductViewModel.productCardsLiveData) {
            handleProductsEvent()
        }
        observe(selectProductViewModel.failure) {
            showToast("Faced an error: $it")
        }

        observe(basketViewModel.stepLiveData) {
            renderCurrentStep(it)
        }
        observe(basketViewModel.eventsLiveData) {
            handleBasketEvents(it)
        }
        observe(basketViewModel.totalSumLiveData) {
            tvTotalValue.text = it.second
            if (it.first) ivSwitchLine.visible() else ivSwitchLine.invisible()
        }
        observe(basketViewModel.failure, ::handleFailure)
    }

    private fun handleBasketEvents(event: ConsumableEvent<BasketViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is BasketViewModel.Event.Close -> consume {
                    finish()
                }
                is BasketViewModel.Event.ShowConfirmCloseDialog -> consume {
                    showConfirmDialog()
                }
                is BasketViewModel.Event.ShowBasketPopup -> consume {
                    renderBasketDialog()
                }
                is BasketViewModel.Event.ShowNetworkMissingDialog -> consume {
                    showNetworkMissingDialog()
                }
                is BasketViewModel.Event.NavigateToMyCards -> consume {
                    MainActivity.returnToActivity(this, shouldGoToCards = true)
                    finish()
                }
                else -> false
            }
        }
    }

    private fun showNetworkMissingDialog() {
        if (networkMissingDialog?.isShowing != true) {
            networkMissingDialog = AlertDialog.Builder(this)
                .setMessage(R.string.payment_missing_network_message)
                .setPositiveButton(R.string.popup_text_ok) { _: DialogInterface, _: Int ->
                    basketViewModel.onMissingDialogCloseClicked()
                }
                .show()
        }
    }

    private fun renderBasketDialog() {
        BasketBottomSheetDialogFragment()
            .show(supportFragmentManager, "basket_dialog_fragment")
    }

    private fun handleProductsEvent() {
        setupViewPager()
    }

    override fun onBackPressed() {
        basketViewModel.onBackPressed()
    }

    private fun intiViews() {
        setSupportActionBar(toolbar)

        toolbar.setTitle(R.string.button_buy_product)
        toolbar.setNavigationOnClickListener {
            basketViewModel.onCloseScreenClicked()
        }

        cvTotalSum.setOnTouchListener(SwipeGestureListener.OnSwipeTouchListener(this) {
            basketViewModel.onShowBasketClicked()
        })
    }

    private fun renderCurrentStep(step: BasketViewModel.PurchaseStep) {
        when (step) {
            BasketViewModel.PurchaseStep.Select -> {
                selectStepText(tvSelectCard)
                updateProgress(PROGRESS_BAR_SELECT_STEP_VALUE)

                vpFragmentsContainer.currentItem = VIEW_PAGER_SELECT_POSITION
                cvTotalSum.visible()
            }
            BasketViewModel.PurchaseStep.Confirm -> {
                selectStepText(tvConfirmOrder)
                updateProgress(PROGRESS_BAR_CONFIRM_STEP_VALUE)

                vpFragmentsContainer.currentItem = VIEW_PAGER_CONFIRM_POSITION
                cvTotalSum.gone()
            }
            BasketViewModel.PurchaseStep.Payment -> {
                selectStepText(tvPayment)
                updateProgress(PROGRESS_BAR_PAYMENT_STEP_VALUE)

                vpFragmentsContainer.currentItem = VIEW_PAGER_PAYMENT_POSITION
            }
            BasketViewModel.PurchaseStep.Complete -> {
                selectStepText(tvCompleteOrder)
                updateProgress(PROGRESS_BAR_COMPLETE_STEP_VALUE)

                vpFragmentsContainer.currentItem = VIEW_PAGER_COMPLETE_POSITION
            }
        }
    }

    private fun setupViewPager() {
        if (!vpFragmentsContainer.isVisible()) {
            pbPurchaseProgressBar.gone()
            vpFragmentsContainer.visible()
        }

        val pagerAdapter = PurchasePagerAdapter(supportFragmentManager)
        vpFragmentsContainer.adapter = pagerAdapter
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.purchase_popup_message)
            .setPositiveButton(R.string.purchase_popup_confirm) { _: DialogInterface, _: Int ->
                finish()
            }.setNegativeButton(R.string.purchase_popup_cancel) { _: DialogInterface, _: Int -> }
            .show()
    }

    private fun selectStepText(textView: TextView) {
        val regularTypeFace = ResourcesCompat.getFont(this, R.font.muli_regular)
        val blackTypeFace = ResourcesCompat.getFont(this, R.font.muli_black)

        tvSelectCard.typeface = regularTypeFace
        tvConfirmOrder.typeface = regularTypeFace
        tvPayment.typeface = regularTypeFace
        tvCompleteOrder.typeface = regularTypeFace

        textView.typeface = blackTypeFace
    }

    private fun updateProgress(value: Int) {
        pbPurchaseProgress.progress = value
    }

    companion object {
        const val PROGRESS_BAR_SELECT_STEP_VALUE = 25
        const val PROGRESS_BAR_CONFIRM_STEP_VALUE = 50
        const val PROGRESS_BAR_PAYMENT_STEP_VALUE = 75
        const val PROGRESS_BAR_COMPLETE_STEP_VALUE = 100

        const val VIEW_PAGER_SELECT_POSITION = 0
        const val VIEW_PAGER_CONFIRM_POSITION = 1
        const val VIEW_PAGER_PAYMENT_POSITION = 2
        const val VIEW_PAGER_COMPLETE_POSITION = 3

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PurchaseActivity::class.java))
        }
    }

    class PurchasePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(position: Int) = when (position) {
            VIEW_PAGER_SELECT_POSITION -> SelectProductCardFragment()
            VIEW_PAGER_CONFIRM_POSITION -> ConfirmFragment()
            VIEW_PAGER_PAYMENT_POSITION -> PaymentFragment()
            VIEW_PAGER_COMPLETE_POSITION -> OrderCompleteFragment()
            else -> throw IllegalArgumentException("Index $position is not supported.")
        }

        override fun getCount() = 4
    }
}
