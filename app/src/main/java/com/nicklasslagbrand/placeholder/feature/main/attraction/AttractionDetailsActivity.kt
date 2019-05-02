package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionDetailsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.extension.centerMarker
import com.nicklasslagbrand.placeholder.extension.fromHtml
import com.nicklasslagbrand.placeholder.extension.loadImageWithFitCenterTransform
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.openDirections
import com.nicklasslagbrand.placeholder.extension.openUrl
import com.nicklasslagbrand.placeholder.extension.setCategoryColorAndTitle
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator
import kotlinx.android.synthetic.main.activity_attraction_details.*
import org.koin.android.viewmodel.ext.android.viewModel

class AttractionDetailsActivity : BaseActivity() {
    private val viewModel: AttractionDetailsViewModel by viewModel()

    private var googleMap: GoogleMap? = null
    private lateinit var markerCreator: MarkerCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_attraction_details)

        initializeToolbar()

        markerCreator = MarkerCreator(this)

        initMap(savedInstanceState)

        observe(viewModel.eventsLiveData) {
            handleEvent(it)
        }
        observe(viewModel.failure, ::handleFailure)

        val attractionId = intent.getIntExtra(ATTRACTION_ID_KEY, 0)

        viewModel.initialize(attractionId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initMap(savedInstanceState: Bundle?) {
        mvStaticMap.onCreate(savedInstanceState)

        mvStaticMap.getMapAsync {
            googleMap = it

            googleMap?.uiSettings?.isMapToolbarEnabled = false
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)

        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleEvent(event: ConsumableEvent<AttractionDetailsViewModel.Event>) {
        event.handleIfNotConsumed {
            when (it) {
                is AttractionDetailsViewModel.Event.RenderFavourite -> {
                    renderFavourite(it.isInFavourites)
                }
                is AttractionDetailsViewModel.Event.RenderAttraction -> {
                    renderAttraction(it.attraction)
                }
                is AttractionDetailsViewModel.Event.OpenUrl -> {
                    openUrl(it.url)
                }
                is AttractionDetailsViewModel.Event.ShowAttractionNotFoundMessage -> {
                    showToast("Attraction with id '${it.attractionId}' not found.")
                }
                is AttractionDetailsViewModel.Event.CloseScreen -> finish()
            }

            true
        }
    }

    private fun renderFavourite(inFavourites: Boolean) {
        val drawable = if (inFavourites) {
            ContextCompat.getDrawable(this, R.drawable.ic_favorite)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_favorite_default)
        }

        btnAddRemoveFavorites.setImageDrawable(drawable)
    }

    private fun renderAttraction(attraction: Attraction) {
        ivAttractionImage.loadImageWithFitCenterTransform(attraction.images.first())
        attraction.category.setCategoryColorAndTitle(tvCategoryTitle)
        tvCategoryTitle.visible()

        ohvAttractionOpening.setOpeningHourItems(attraction.openingHours)
        tvAttractionTitle.text = attraction.title
        tvAttractionAddress.text = attraction.contactInfo.address

        tvAttractionLink.text = attraction.contactInfo.web

        tvAttractionAnotherTitle.text = attraction.title
        tvAttractionTeaser.text = attraction.teaser.fromHtml()
        tvAttractionDescription.text = attraction.description.fromHtml()

        btnAddRemoveFavorites.setOnClickListener {
            viewModel.onAddRemoveFavouriteClicked()
        }
        btnGetDirections.setOnClickListener {
            openDirections(attraction.location)
        }

        tvAttractionLink.setOnClickListener {
            viewModel.onAttractionLinkClicked()
        }

        renderStaticMap(attraction)
    }

    private fun renderStaticMap(attraction: Attraction) {
        googleMap?.let {
            val markerOptions = markerCreator.buildMarker(attraction.category.id, attraction.location, true)
            val marker = it.addMarker(markerOptions)

            it.centerMarker(marker)
        }
    }

    companion object {
        const val ATTRACTION_ID_KEY = "attraction_id"

        fun startActivity(context: Context, attractionId: Int) {
            context.startActivity(
                Intent(context, AttractionDetailsActivity::class.java)
                    .putExtra(ATTRACTION_ID_KEY, attractionId)
            )
        }
    }
}
