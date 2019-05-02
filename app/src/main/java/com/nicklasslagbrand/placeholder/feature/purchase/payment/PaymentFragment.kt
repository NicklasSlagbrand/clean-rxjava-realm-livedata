package com.nicklasslagbrand.placeholder.feature.purchase.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PaymentFragment : Fragment() {
    private val basketViewModel: BasketViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        subscribeToLiveData()
    }

    private fun initViews() {}

    private fun subscribeToLiveData() {
    }

    private fun initializePayment() {
    }
}
