package com.nicklasslagbrand.placeholder.feature.purchase.basket

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.extension.gone
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.visible
import kotlinx.android.synthetic.main.item_basket_product.view.*
import kotlinx.android.synthetic.main.item_basket_total.view.*

class BasketAdapter(private val isInEditMode: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var removalListener: ProductRemovalListener? = null
    private var items = mutableListOf<ListItem>()

    fun setItems(newItems: List<ListItem>) {
        items = newItems.toMutableList()

        notifyDataSetChanged()
    }

    fun setOnRemovalListener(onRemovalListener: ProductRemovalListener) {
        removalListener = onRemovalListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PRODUCT_ITEM_TYPE -> ProductViewHolder(parent.inflate(R.layout.item_basket_product))
            TOTAL_ITEM_TYPE -> TotalViewHolder(parent.inflate(R.layout.item_basket_total))
            else -> throw IllegalStateException("Only two item types are supported.")
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.ProductItem -> PRODUCT_ITEM_TYPE
            is ListItem.TotalItem -> TOTAL_ITEM_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is ListItem.ProductItem -> {
                (holder as ProductViewHolder).bind(item, isInEditMode, removalListener)
            }
            is ListItem.TotalItem -> {
                (holder as TotalViewHolder).bind(item, isInEditMode)
            }
        }
    }

    companion object {
        const val PRODUCT_ITEM_TYPE = 0
        const val TOTAL_ITEM_TYPE = 1
    }

    inner class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ListItem.TotalItem, isInEditMode: Boolean) {
            itemView.tvTotalSum.text = item.sum

            if (isInEditMode) {
                itemView.vRemoveButtonPlaceHolder.visible()
            } else {
                itemView.vRemoveButtonPlaceHolder.gone()
            }
        }
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ListItem.ProductItem, isInEditMode: Boolean, onRemovalListener: ProductRemovalListener?) {
            itemView.tvProductName.text = item.title
            itemView.tvProductSum.text = item.sum

            if (isInEditMode) {
                itemView.fbRemoveItem.visible()

                itemView.fbRemoveItem.setOnClickListener {
                    onRemovalListener?.onRemoveProductClicked(item)
                }
            } else {
                itemView.fbRemoveItem.gone()
            }
        }
    }
}
