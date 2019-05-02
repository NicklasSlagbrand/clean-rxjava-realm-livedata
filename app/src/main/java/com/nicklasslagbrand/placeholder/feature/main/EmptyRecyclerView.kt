package com.nicklasslagbrand.placeholder.feature.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class EmptyRecyclerView : RecyclerView {
    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attr: AttributeSet?) : this(ctx, attr, 0)
    constructor(ctx: Context, attr: AttributeSet?, defStyle: Int) : super(ctx, attr, defStyle)

    var emptyView: View? by Delegates.observable<View?>(null) { _, _, _ ->
        checkIfEmpty()
    }

    fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {
            val emptyViewVisible = adapter?.itemCount == 0
            emptyView?.visibility = if (emptyViewVisible) View.VISIBLE else View.INVISIBLE
            visibility = if (emptyViewVisible) View.INVISIBLE else View.VISIBLE
        }
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)

        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }
}
