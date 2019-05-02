package com.nicklasslagbrand.placeholder.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.extension.getLogo
import com.nicklasslagbrand.placeholder.extension.getTitle
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.visible
import kotlinx.android.synthetic.main.item_category.view.*
import kotlin.properties.Delegates

class CategoriesAdapter(val context: Context, private val clickListener: (List<AttractionCategory>) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private val selectedCategories = mutableListOf<AttractionCategory>()

    var categories: List<AttractionCategory> by Delegates.observable(
        emptyList()
    ) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectableClickListener: (AttractionCategory) -> Unit = {
            if (selectedCategories.contains(it)) {
                selectedCategories.remove(it)
            } else {
                selectedCategories.add(it)
            }

            clickListener(selectedCategories)

            notifyDataSetChanged()
        }

        val category = categories[position]
        val isSelected = selectedCategories.contains(category)

        holder.bind(category, selectableClickListener, isSelected)
    }

    override fun getItemCount() = categories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: AttractionCategory, clickListener: (AttractionCategory) -> Unit, isSelected: Boolean) {
            val context = itemView.context

            itemView.apply {
                tvCategoryTitle.text = category.getTitle(context)
                ivCategoryLogo.setImageDrawable(category.getLogo(context))

                if (isSelected) ivCheckMark.visible() else ivCheckMark.invisible()

                setOnClickListener {
                    clickListener(category)
                }
            }
        }
    }
}
