package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.FavouritesViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.FavouritesViewModel.Event
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.extension.gone
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.loadImageWithFitCenterTransform
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.item_favourite.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavouritesFragment : BaseFragment() {
    private val viewModel: FavouritesViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_favourites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        subscribeToLiveData()
    }

    override fun onResume() {
        super.onResume()

        viewModel.initialize()
    }

    private fun initViews() {
        rvFavouriteAttractionList.adapter = FavouritesAdapter().apply {
            clickListener = {
                AttractionDetailsActivity.startActivity(requireActivity(), it.id)
            }
            removalListener = {
                viewModel.onRemoveFavouriteClicked(it)
            }
        }
    }

    private fun subscribeToLiveData() {
        observe(viewModel.eventsLiveData) {
            handleEvents(it)
        }
        observe(viewModel.failure, ::handleFailure)
    }

    private fun handleEvents(event: ConsumableEvent<Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is Event.ShowFavouritesList -> {
                    showFavouritesView()
                    (rvFavouriteAttractionList.adapter as FavouritesAdapter).setItems(it.favourites)
                    true
                }
                is Event.NoFavourites -> {
                    showNoFavouritesView()
                    true
                }
            }
        }
    }

    private fun showFavouritesView() {
        layoutNoFavourites.gone()

        rvFavouriteAttractionList.visible()
        pbFavouritesProgressBar.gone()
    }

    private fun showNoFavouritesView() {
        pbFavouritesProgressBar.gone()

        rvFavouriteAttractionList.gone()
        layoutNoFavourites.visible()
    }

    inner class FavouritesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var removalListener: (Attraction) -> Unit = {}
        var clickListener: (Attraction) -> Unit = {}

        private var items = listOf<Attraction>()

        fun setItems(newItems: List<Attraction>) {
            items = newItems

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return FavoriteViewHolder(parent.inflate(R.layout.item_favourite))
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            (holder as FavoriteViewHolder).bind(item)
        }

        inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Attraction) {
                itemView.tvAttractionTitle.text = item.title
                itemView.ivAttractionImage.loadImageWithFitCenterTransform(item.images.first())

                itemView.setOnClickListener {
                    clickListener(item)
                }

                itemView.btnAddRemoveFavorites.setOnClickListener {
                    removalListener(item)
                }
            }
        }
    }
}
