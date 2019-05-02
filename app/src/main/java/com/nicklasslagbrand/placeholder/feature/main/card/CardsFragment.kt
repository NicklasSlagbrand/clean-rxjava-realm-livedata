package com.nicklasslagbrand.placeholder.feature.main.card

import android.app.PendingIntent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager.widget.ViewPager
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.Constants.Companion.APP_CARDS_NOTIFICATION_CHANNEL
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.CardsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import com.nicklasslagbrand.placeholder.feature.main.MainActivity
import com.nicklasslagbrand.placeholder.feature.purchase.PurchaseActivity
import com.nicklasslagbrand.placeholder.view.SlideToActView
import kotlinx.android.synthetic.main.dialog_activate_card.view.*
import kotlinx.android.synthetic.main.dialog_activation_failed.view.*
import kotlinx.android.synthetic.main.dialog_activation_successful.view.*
import kotlinx.android.synthetic.main.fragment_cards.*
import me.crosswall.lib.coverflow.CoverFlow
import org.koin.android.viewmodel.ext.android.viewModel

class CardsFragment : BaseFragment() {
    private val viewModel: CardsViewModel by viewModel()

    private var currentlyShownDialog: AlertDialog? = null

    private var cardsViewPager: CardsViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_cards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToLiveData()

        viewModel.initialize()
    }

    private fun subscribeToLiveData() {
        observe(viewModel.eventsLiveData) {
            handleEvents(it)
        }
        observe(viewModel.cardsLiveData) {
            handleCards(it)
        }
        observe(viewModel.failure, ::handleFailure)
    }

    private fun handleEvents(event: ConsumableEvent<CardsViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                CardsViewModel.Event.ShowNoCards -> consume {
                    showNoCardsView()
                }
                CardsViewModel.Event.ShowActivateCardDialog -> consume {
                    showActivateCardDialog()
                }
                CardsViewModel.Event.ShowSuccessfulCardActivation -> consume {
                    showActivationSuccessfulDialog()
                }
                CardsViewModel.Event.ShowFailedCardActivation -> consume {
                    showActivationFailedDialog()
                }
                CardsViewModel.Event.ShowCardsLoading -> consume {
                    showProgress()
                }
                CardsViewModel.Event.SetMaxBrightness -> consume {
                    setBrightness(MAX_BRIGHTNESS_LEVEL)
                }
                CardsViewModel.Event.SetDefaultBrightness -> consume {
                    setBrightness(DEFAULT_BRIGHTNESS_LEVEL)
                }
                CardsViewModel.Event.ShowNewCardNotification -> consume {
                    showNewCardAvailableNotification()
                }
            }
        }
    }

    private fun showNewCardAvailableNotification() {
        val intent = MainActivity.returnToActivityIntent(requireContext(), true)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val notification = NotificationCompat.Builder(requireContext(), APP_CARDS_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.received_card))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVibrate(longArrayOf(0))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(requireContext())) {
            notify(Constants.NEW_CARD_NOTIFICATION_ID, notification)
        }
    }

    private fun handleCards(cards: List<PurchasedCard>) {
        pbCardsProgressBar.invisible()
        vpCardsContainer.visible()

        if (cardsViewPager == null) {
            CoverFlow.Builder()
                .with(vpCardsContainer)
                .scale(CARDS_SCALE)
                .pagerMargin(resources.getDimensionPixelSize(R.dimen.cards_margin).toFloat())
                .build()

            cardsViewPager = CardsViewPager(requireContext(), cards, viewModel)
            vpCardsContainer.adapter = cardsViewPager

            vpCardsContainer.clipChildren = false
            vpCardsContainer.offscreenPageLimit = cards.size

            vpCardsContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    viewModel.onVisibleCardChanged(position)
                }
            })
        } else {
            cardsViewPager?.updateCards(cards)
        }
    }

    private fun showProgress() {
        pbCardsProgressBar.visible()
        vpCardsContainer.invisible()
    }

    private fun showNoCardsView() {
        pbCardsProgressBar.invisible()
        pcCardsContainerLayout.invisible()

        clNoCardViewContainer.visible()

        btnBuyCard.setOnClickListener {
            activity?.let { activity ->
                PurchaseActivity.startActivity(activity)
            }
        }
    }

    private fun showActivationFailedDialog() {
        currentlyShownDialog?.dismiss()

        val dialogView = layoutInflater.inflate(R.layout.dialog_activation_failed, null)

        dialogView.btnRetryActivation.setOnClickListener {
            viewModel.retryActivationClicked()
        }

        dialogView.btnCancelDialog.setOnClickListener {
            currentlyShownDialog?.dismiss()
        }

        currentlyShownDialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

        currentlyShownDialog?.show()
    }

    private fun showActivationSuccessfulDialog() {
        currentlyShownDialog?.dismiss()

        val dialogView = layoutInflater.inflate(R.layout.dialog_activation_successful, null)

        dialogView.btnCloseDialog.setOnClickListener {
            currentlyShownDialog?.dismiss()
        }

        currentlyShownDialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setOnDismissListener {
                viewModel.onSuccessfulDialogDismissed()
            }
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

        currentlyShownDialog?.show()
    }

    private fun showActivateCardDialog() {
        currentlyShownDialog?.dismiss()

        val dialogView = layoutInflater.inflate(R.layout.dialog_activate_card, null)

        currentlyShownDialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

        dialogView.savSlideToActivate.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                dialogView.savSlideToActivate.invisible()
                dialogView.pbActivationProgress.visible()
                viewModel.activateCard()
            }
        }

        dialogView.btnCancelActivation.setOnClickListener {
            currentlyShownDialog?.dismiss()
        }

        currentlyShownDialog?.show()
    }

    fun setBrightness(brightness: Float) {
        val layoutParams = requireActivity().window.attributes
        layoutParams.screenBrightness = brightness
        requireActivity().window.attributes = layoutParams
    }

    companion object {
        const val CARDS_SCALE = 0.3f
        const val MAX_BRIGHTNESS_LEVEL = 1f
        const val DEFAULT_BRIGHTNESS_LEVEL = -1f
    }
}
