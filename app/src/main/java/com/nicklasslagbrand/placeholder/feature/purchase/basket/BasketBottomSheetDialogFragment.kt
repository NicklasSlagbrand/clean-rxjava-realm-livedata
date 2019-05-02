package com.nicklasslagbrand.placeholder.feature.purchase.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.BasketViewModel
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import kotlinx.android.synthetic.main.fragment_confirm.*
import kotlinx.android.synthetic.main.view_purchase_basket.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BasketBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val basketViewModel: BasketViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.view_purchase_basket, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BasketAdapter(isInEditMode = true)

        adapter.setOnRemovalListener(object : ProductRemovalListener {
            override fun onRemoveProductClicked(item: ListItem.ProductItem) {
                basketViewModel.onProductRemovedFromBasket(item.productId)
            }
        })

        rvBasketList.addItemDecoration(SimpleLineDividerItemDecoration(rvBasketList.context, VERTICAL))

        view.rvBasketList.adapter = adapter

        view.btnConfirmOrder.setOnClickListener {
            dismiss()
            basketViewModel.onConfirmClicked()
        }

        observe(basketViewModel.basketItemsLiveData) {
            adapter.setItems(it)
            // If we have only total sum item
            if (it.size > 1) view.ivSwitchLine.visible() else view.ivSwitchLine.invisible()
            view.btnConfirmOrder.isEnabled = it.size > 1 // We always have "Total" item in list so we need to skip it.
        }
    }
}
