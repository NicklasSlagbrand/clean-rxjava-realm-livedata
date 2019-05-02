package com.nicklasslagbrand.placeholder.feature.main.card

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.CardsViewModel
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.extension.empty
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.loadImageWithFitCenterTransform
import com.nicklasslagbrand.placeholder.feature.purchase.PurchaseActivity
import com.nicklasslagbrand.placeholder.feature.transfer.StartTransferCardActivity
import kotlinx.android.synthetic.main.view_card_activated.view.*
import kotlinx.android.synthetic.main.view_card_expired.view.*
import kotlinx.android.synthetic.main.view_card_not_activated.view.*
import kotlinx.android.synthetic.main.view_card_pending.view.*
import kotlinx.android.synthetic.main.view_card_transferred.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class CardsViewPager(val context: Context, private var cards: List<PurchasedCard>, val viewModel: CardsViewModel) :
    PagerAdapter() {

    private val dateFormat: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("dd-MM-yyyy")
    }
    private val timeFormat: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("HH:mm")
    }

    fun updateCards(newCards: List<PurchasedCard>) {
        this.cards = newCards

        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val card = cards[position]

        val view = when (card.status) {
            QrCodeStatus.NotActivated -> initializeNotActiveCardView(card, container)
            QrCodeStatus.Active -> initializeActivatedCardView(card, container)
            QrCodeStatus.Expired -> initializeExpiredCardView(card, container)
            QrCodeStatus.Transferred -> initializeTransferredCardView(card, container)
            QrCodeStatus.Pending -> initializePendingCardView(card, container)
        }

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount() = cards.size

    private fun initializePendingCardView(card: PurchasedCard, container: ViewGroup): View {
        val cardView = container.inflate(R.layout.view_card_pending)

        with(cardView) {
            pcvCardViewPending.setProductHours(String.empty())
            pcvCardViewPending.hideHoursText()

            tvCardPendingExplanationTitle.text =
                context.getString(R.string.cards_overview_pending_explanation_title_android, card.id)

            pcvCardViewPending.deactivate()
        }

        return cardView
    }

    private fun initializeTransferredCardView(card: PurchasedCard, container: ViewGroup): View {
        val cardView = container.inflate(R.layout.view_card_transferred)

        with(cardView) {
            pcvCardViewTransferred.updateTexts(card.validationTime)
            pcvCardViewTransferred.deactivate()
        }

        return cardView
    }

    private fun initializeExpiredCardView(card: PurchasedCard, container: ViewGroup): View {
        val cardView = container.inflate(R.layout.view_card_expired)

        with(cardView) {
            btnBuyCard.setOnClickListener {
                PurchaseActivity.startActivity(context)
            }

            pcvCardViewExpired.updateTexts(card.validationTime)
            pcvCardViewExpired.deactivate()
        }

        return cardView
    }

    @SuppressLint("SetTextI18n")
    private fun initializeActivatedCardView(card: PurchasedCard, container: ViewGroup): View {
        val cardView = container.inflate(R.layout.view_card_activated)

        val dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(card.expirationDate)

        with(cardView) {
            btnTransferCardActivated.setOnClickListener {
                StartTransferCardActivity.startActivity(context, card.id)
            }

            pcvCardViewActivated.updateTexts(card.validationTime)
            tvCardExpirationDate.text = dateTime.toString(dateFormat)
            tvCardExpirationTime.text =
                "${context.getString(R.string.cards_overview_active_card_time_prefix)} ${dateTime.toString(timeFormat)}"

            val cornerRadiusInPixels =
                context.resources.getDimensionPixelSize(R.dimen.activate_card_qr_code_corner_radius)
            ivCardBarCode.loadImageWithFitCenterTransform(
                card.qrCodeUrl,
                RequestOptions.fitCenterTransform(),
                RequestOptions.bitmapTransform(RoundedCorners(cornerRadiusInPixels))
            )

            pcvCardViewActivated.activate()
        }

        return cardView
    }

    private fun initializeNotActiveCardView(card: PurchasedCard, container: ViewGroup): View {
        val cardView = container.inflate(R.layout.view_card_not_activated)

        with(cardView) {
            btnActivateCard.setOnClickListener {
                viewModel.activateCardClicked(card)
            }
            btnTransferCard.setOnClickListener {
                StartTransferCardActivity.startActivity(context, card.id)
            }

            pcvCardView.updateTexts(card.validationTime)

            pcvCardView.deactivate()
        }

        return cardView
    }
}
