package com.nicklasslagbrand.placeholder.feature.purchase.confirm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConfirmViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.extension.openUrl
import com.nicklasslagbrand.placeholder.extension.hideKeyboard
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import com.nicklasslagbrand.placeholder.feature.purchase.basket.BasketAdapter
import com.nicklasslagbrand.placeholder.feature.purchase.basket.ListItem
import com.nicklasslagbrand.placeholder.feature.purchase.basket.SimpleLineDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_confirm.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ConfirmFragment : BaseFragment() {
    private val viewModel: ConfirmViewModel by sharedViewModel()
    private val basketViewModel: BasketViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_confirm, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.basketViewModel = basketViewModel

        observe(viewModel.eventsLiveData) {
            handleEvent(it)
        }
        observe(viewModel.failure, ::handleFailure)

        observe(basketViewModel.basketItemsLiveData) {
            populateBasketItems(it)
        }
        observe(basketViewModel.eventsLiveData) {
            handleBasketEvents(it)
        }

        initViews()
    }

    private fun handleBasketEvents(event: ConsumableEvent<BasketViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is BasketViewModel.Event.ShowConfirmProgress -> {
                    if (it.showProgress) {
                        pbConfirmProgressBar.visible()
                        btnToPayment.invisible()
                    } else {
                        pbConfirmProgressBar.invisible()
                        btnToPayment.visible()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun populateBasketItems(items: List<ListItem>) {
        val adapter = BasketAdapter(isInEditMode = false)
        adapter.setItems(items)

        rvBasketList.adapter = adapter

        rvBasketList.addItemDecoration(SimpleLineDividerItemDecoration(rvBasketList.context, VERTICAL))
        rvBasketList.setHasFixedSize(true)
        rvBasketList.isNestedScrollingEnabled = false
    }

    private fun initViews() {
        btnToPayment.isEnabled = false
        btnToPayment.setOnClickListener {
            activity?.hideKeyboard()
            viewModel.onPaymentButtonClicked()
        }

        btnTermsAndConditions.setOnClickListener {
            viewModel.onTermsAndConditionsClicked()
        }

        cbTermsAndConditions.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onTermsAndConditionsCheckChanged(isChecked)
        }

        tieUserName.addTextChangedListener(object : SimpleTextChangedListener() {
            override fun afterTextChanged(s: Editable) {
                viewModel.onUserNameChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                tilUserName.isErrorEnabled = false
            }
        })

        tieUserEmail.addTextChangedListener(object : SimpleTextChangedListener() {
            override fun afterTextChanged(s: Editable) {
                viewModel.onUserEmailChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                tilUserEmail.isErrorEnabled = false
            }
        })

        tieUserConfirmEmail.addTextChangedListener(object : SimpleTextChangedListener() {
            override fun afterTextChanged(s: Editable) {
                viewModel.onUserConfirmEmailChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                tilUserConfirmEmail.isErrorEnabled = false
            }
        })
    }

    private fun handleEvent(event: ConsumableEvent<ConfirmViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is ConfirmViewModel.Event.NameFieldError -> {
                    tilUserName.error = getString(R.string.confirm_name_error)
                    tilUserName.isErrorEnabled = true

                    smoothScrollToTheScreenTop()
                    true
                }
                is ConfirmViewModel.Event.EmailFieldError -> {
                    tilUserEmail.error = getString(R.string.confirm_email_error)
                    tilUserEmail.isErrorEnabled = true

                    smoothScrollToTheScreenTop()
                    true
                }
                is ConfirmViewModel.Event.ConfirmEmailFieldError -> {
                    tilUserConfirmEmail.error = getString(R.string.confirm_email_error)
                    tilUserConfirmEmail.isErrorEnabled = true

                    smoothScrollToTheScreenTop()
                    true
                }
                is ConfirmViewModel.Event.PaymentButtonEnabled -> {
                    btnToPayment.isEnabled = true
                    true
                }
                is ConfirmViewModel.Event.PaymentButtonDisabled -> {
                    btnToPayment.isEnabled = false
                    true
                }
                is ConfirmViewModel.Event.NavigateToTermsAndConditions -> {
                    activity?.openUrl(it.url)
                    true
                }
            }
        }
    }

    private fun smoothScrollToTheScreenTop() {
        nsvScrollContainer.smoothScrollTo(0, 0)
    }

    abstract class SimpleTextChangedListener : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            /* no-op */
        }
    }
}
