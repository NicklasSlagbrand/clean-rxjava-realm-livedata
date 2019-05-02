package com.nicklasslagbrand.placeholder.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import kotlinx.android.synthetic.main.item_search_result.view.*
import kotlin.properties.Delegates

class SearchAdapter(val context: Context, private val clickListener: (Attraction) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var results: List<Attraction> by Delegates.observable(
        emptyList()
    ) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(
            R.layout.item_search_result,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(results[position], clickListener)
    }

    override fun getItemCount() = results.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSearchResult: TextView = view.tvSearchResult

        fun bind(attraction: Attraction, clickListener: (Attraction) -> Unit) {
            tvSearchResult.text = attraction.title

            itemView.setOnClickListener { clickListener(attraction) }
        }
    }
}
