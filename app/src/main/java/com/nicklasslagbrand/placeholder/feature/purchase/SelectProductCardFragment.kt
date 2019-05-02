package com.nicklasslagbrand.placeholder.feature.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.SelectProductViewModel
import com.nicklasslagbrand.placeholder.domain.model.Currency
import com.nicklasslagbrand.placeholder.domain.model.Product
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.isVisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import com.nicklasslagbrand.placeholder.feature.purchase.card.PeopleSelectionView
import com.nicklasslagbrand.placeholder.feature.purchase.card.ProductCardView
import kotlinx.android.synthetic.main.fragment_select_card.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SelectProductCardFragment : BaseFragment(), View.OnClickListener {
    private val selectProductViewModel: SelectProductViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_select_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeopleCounters()

        setupChangeCurrencyButton()

        setupAddToBasketButton()

        observe(selectProductViewModel.productCardsLiveData) {
            setupProductCardsViews()

            renderProducts(it)
        }
        observe(selectProductViewModel.eventLiveData) {
            handleEvent(it)
        }
        observe(selectProductViewModel.adultCounterLiveData) {
            handlePeopleCounterEvent(it, pvSelection)
        }
        observe(selectProductViewModel.failure, ::handleFailure)
    }

    private fun setupAddToBasketButton() {
        btAddToBasket.isEnabled = false
        btAddToBasket.setOnClickListener {
            selectProductViewModel.onAddToBasketClicked()
        }
    }

    private fun setupChangeCurrencyButton() {
        btChangeCurrency.setOnClickListener {
            selectProductViewModel.onCurrencyChangeRequest()
        }
    }

    private fun handleEvent(event: ConsumableEvent<SelectProductViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is SelectProductViewModel.Event.ShowCurrencyDialog -> {
                    showSelectCurrencyDialog(it.currencies, it.currentCurrency)
                    true
                }
                is SelectProductViewModel.Event.CurrencyChanged -> {
                    updateCurrencyButtonText(it.newCurrencySymbol)
                    true
                }
                is SelectProductViewModel.Event.AddBasketButtonState -> {
                    btAddToBasket.isEnabled = it.isEnabled
                    true
                }
                is SelectProductViewModel.Event.ClearSelectedCards -> {
                    unSelectAllCards()

                    true
                }
            }
        }
    }

    private fun renderProducts(products: List<Product>) {
        val hour24Product = products[PRODUCT_24_HOUR_INDEX]
        val hour48Product = products[PRODUCT_48_HOUR_INDEX]
        val hour72Product = products[PRODUCT_72_HOUR_INDEX]
        val hour120Product = products[PRODUCT_120_HOUR_INDEX]

        cv24Hours.setProductHours(hour24Product.validationTime.toString())
        cv24Hours.tag = hour24Product.id

        cv48Hours.setProductHours(hour48Product.validationTime.toString())
        cv48Hours.tag = hour48Product.id

        cv72Hours.setProductHours(hour72Product.validationTime.toString())
        cv72Hours.tag = hour72Product.id

        cv120Hours.setProductHours(hour120Product.validationTime.toString())
        cv120Hours.tag = hour120Product.id
    }

    private fun handlePeopleCounterEvent(event: PeopleSectionData, peopleSelectionView: PeopleSelectionView) {
        peopleSelectionView.setTotalSum(event.sumText)
        peopleSelectionView.setPeopleCount(event.count)

        peopleSelectionView.enableRemoveButton(event.removeEnabled)
        peopleSelectionView.enableAddButton(event.addEnabled)
    }

    private fun setupPeopleCounters() {
        pvSelection.setSectionTitle(getString(R.string.purchase_category2_group_text))
        pvSelection.onAddClicked {
            selectProductViewModel.onAdultAdded()
        }
        pvSelection.onRemoveClicked {
            selectProductViewModel.onAdultRemoved()
        }
    }

    private fun setupProductCardsViews() {
        cv24Hours.setOnClickListener(this)
        cv48Hours.setOnClickListener(this)
        cv72Hours.setOnClickListener(this)
        cv120Hours.setOnClickListener(this)
    }

    private fun unSelectAllCards() {
        if (clSectionContainer.isVisible()) {
            clSelectACardContainer.visible()
            clSectionContainer.invisible()
        }

        cv24Hours.select()
        cv48Hours.select()
        cv72Hours.select()
        cv120Hours.select()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrencyButtonText(newCurrencySymbol: String) {
        btChangeCurrency.text = "${getString(R.string.change_currency_button)} $newCurrencySymbol"
    }

    private fun showSelectCurrencyDialog(currencies: List<Currency>, currentCurrency: Currency) {
        activity?.let { activity ->
            val currentPosition = currencies.indexOf(currentCurrency)
            val currenciesNames = currencies.map { it.currencyName }.toTypedArray()

            var selectedCurrency: Currency? = null
            AlertDialog.Builder(activity)
                .setTitle(R.string.select_button)
                .setSingleChoiceItems(currenciesNames, currentPosition) { _, which ->
                    selectedCurrency = currencies[which]
                }.setPositiveButton(R.string.popup_text_ok) { _: DialogInterface, _: Int ->
                    selectedCurrency?.let {
                        selectProductViewModel.onCurrencySelected(it)
                    }
                }.setNegativeButton(R.string.cancel_button) { _: DialogInterface, _: Int ->
                }
                .show()
        }
    }

    override fun onClick(carView: View) {
        if (carView is ProductCardView) {
            if (!clSectionContainer.isVisible()) {
                clSelectACardContainer.invisible()
                clSectionContainer.visible()
            }

            cv24Hours.unSelect()
            cv48Hours.unSelect()
            cv72Hours.unSelect()
            cv120Hours.unSelect()

            carView.select()

            selectProductViewModel.onProductSelected(carView.tag as Int)
        }
    }

    data class PeopleSectionData(
        val count: Int,
        val sumText: String,
        val removeEnabled: Boolean,
        val addEnabled: Boolean
    )

    companion object {
        const val PRODUCT_24_HOUR_INDEX = 0
        const val PRODUCT_48_HOUR_INDEX = 1
        const val PRODUCT_72_HOUR_INDEX = 2
        const val PRODUCT_120_HOUR_INDEX = 3
    }
}
