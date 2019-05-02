package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.extension.fromHtml
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.loadImageWithCenterCrop
import kotlinx.android.synthetic.main.item_content.view.*
import kotlin.properties.Delegates

class ContentAdapter(val context: Context) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
    var clickListener: (Attraction) -> Unit = {}
    var addRemoveListener: (FavouriteAttraction) -> Unit = {}

    var attractions: List<FavouriteAttraction> by Delegates.observable(
        emptyList()
    ) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_content))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favouriteAttraction = attractions[position]

        holder.bind(favouriteAttraction, clickListener, addRemoveListener)
    }

    override fun getItemCount() = attractions.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            favouriteAttraction: FavouriteAttraction,
            clickListener: (Attraction) -> Unit,
            addRemovalListener: (FavouriteAttraction) -> Unit
        ) {
            itemView.apply {
                with(favouriteAttraction.attraction) {
                    ivPreview.loadImageWithCenterCrop(images.first())
                    tvIntroTitle.text = title
                    tvDescription.text = description.fromHtml()

                    setOnClickListener {
                        clickListener(this)
                    }
                }

                showFavouriteIcon(favouriteAttraction.isFavourite, btnAddRemoveFavorites)
                btnAddRemoveFavorites.setOnClickListener { view ->
                    with(favouriteAttraction) {
                        isFavourite = !isFavourite

                        showFavouriteIcon(isFavourite, view)
                        addRemovalListener(this)
                    }
                }
            }
        }

        private fun showFavouriteIcon(isFavourite: Boolean, view: View) {
            val drawable = if (isFavourite) {
                ContextCompat.getDrawable(view.context, R.drawable.ic_favorite)
            } else {
                ContextCompat.getDrawable(view.context, R.drawable.ic_favorite_default)
            }
            view.btnAddRemoveFavorites.setImageDrawable(drawable)
        }
    }
}
