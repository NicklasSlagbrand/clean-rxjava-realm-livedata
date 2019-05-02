package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.extension.getTitle
import com.nicklasslagbrand.placeholder.extension.inflate
import kotlinx.android.synthetic.main.item_content_section.view.*
import kotlin.properties.Delegates

class ContentSectionAdapter(val context: Context) : RecyclerView.Adapter<ContentSectionAdapter.ViewHolder>() {
    var clickListener: (Attraction) -> Unit = {}
    var addRemoveListener: (FavouriteAttraction) -> Unit = {}

    var sections: List<SectionsItem> by Delegates.observable(
        emptyList()
    ) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_content_section))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]

        holder.bind(section, clickListener, addRemoveListener)
    }

    override fun getItemCount() = sections.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            section: SectionsItem,
            listener: (Attraction) -> Unit,
            addRemoveListener: (FavouriteAttraction) -> Unit
        ) {
            val context = itemView.context
            itemView.apply {
                tvSectionTitle.text = AttractionCategory.forId(section.id).getTitle(context)

                val contentAdapter = ContentAdapter(context).apply {
                    clickListener = {
                        listener(it)
                    }
                    this.addRemoveListener = {
                        addRemoveListener(it)
                    }
                }

                rvCardsContent.setHasFixedSize(true)
                rvCardsContent.adapter = contentAdapter
                contentAdapter.attractions = section.items
            }
        }
    }
}
