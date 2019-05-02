package com.nicklasslagbrand.placeholder.feature.purchase.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import com.nicklasslagbrand.placeholder.extension.observe
import kotlinx.android.synthetic.main.fragment_order_complete.*
import org.joda.time.format.DateTimeFormat
import org.koin.android.viewmodel.ext.android.sharedViewModel

class OrderCompleteFragment : Fragment() {
    private val basketViewModel: BasketViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_order_complete, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToLiveData()

        btnToCards.setOnClickListener {
            basketViewModel.onGotToCardsClicked()
        }
    }

    private fun subscribeToLiveData() {
        observe(basketViewModel.paymentSuccessfulLiveData) {
            initializeViews(it)
        }
    }

    private fun initializeViews(data: SuccessfulPaymentData) {
        with(data) {
            tvOrderId.text = orderId
            tvOrderReference.text = orderReference
            tvOrderAmount.text = amount
            tvOrderCurrency.text = currency

            tvOrderDate.text = formatDate(date)
        }
    }

    private fun formatDate(date: String): String {
        val dateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(date)
        return dateTime.toString(DateTimeFormat.forPattern("dd-MM-yyyy"))
    }
}
