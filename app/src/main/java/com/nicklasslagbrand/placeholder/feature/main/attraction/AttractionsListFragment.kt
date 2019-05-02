package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionsListViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.extension.getTitle
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_attractions_list.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AttractionsListFragment : BaseFragment() {
    private val attractionsListViewModel: AttractionsListViewModel by sharedViewModel()
    private var adapter: ContentSectionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_attractions_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        subscribeToLiveData()
    }

    private fun initRecyclerView() {
        adapter = ContentSectionAdapter(requireContext()).apply {
            clickListener = { attraction ->
                moveToDetailsScreen(attraction)
            }
            addRemoveListener = { favouriteAttraction ->
                attractionsListViewModel.addRemoveToFavorite(favouriteAttraction)
            }
        }
        rvAttractionSections.adapter = adapter
    }

    private fun moveToDetailsScreen(attraction: Attraction) {
        activity?.let {
            AttractionDetailsActivity.startActivity(it, attraction.id)
        }
    }

    private fun subscribeToLiveData() {
        observe(attractionsListViewModel.sectionsLiveData) {
            handleAttractions(it)
        }
        observe(attractionsListViewModel.eventsLiveData) {
            handleFavouriteEvents(it)
        }
        observe(attractionsListViewModel.failure, ::handleFailure)

        attractionsListViewModel.fetchSectionsWithAttractions()
    }

    private fun handleFavouriteEvents(event: ConsumableEvent<AttractionsListViewModel.FavouriteEvent>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is AttractionsListViewModel.FavouriteEvent.FailureDuringAddAction -> {
                    showToast(getString(R.string.failure_add_attraction_in_favourite))

                    true
                }
                is AttractionsListViewModel.FavouriteEvent.FailureDuringRemovalAction -> {
                    showToast(getString(R.string.failure_remove_attraction_from_favourite))

                    true
                }
            }
        }
    }

    private fun handleAttractions(event: ConsumableEvent<List<SectionsItem>>) {
        event.handleIfNotConsumed {
            val sections = it.sortedBy { section ->
                AttractionCategory.forId(section.id).getTitle(requireContext())
            }
            adapter?.sections = sections

            true
        }
    }
}
