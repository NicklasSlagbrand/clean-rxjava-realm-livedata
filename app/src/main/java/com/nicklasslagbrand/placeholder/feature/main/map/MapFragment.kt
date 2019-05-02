package com.nicklasslagbrand.placeholder.feature.main.map

import android.Manifest
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.AttractionsViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.MapViewModel
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.extension.centerMarker
import com.nicklasslagbrand.placeholder.extension.createMarkers
import com.nicklasslagbrand.placeholder.extension.invisible
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseFragment
import com.nicklasslagbrand.placeholder.view.SearchableView
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@SuppressWarnings("TooManyFunctions", "LargeClass")
open class MapFragment : BaseFragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
    SearchableView.OnResultSelectedListener, GoogleMap.OnMarkerClickListener,
    SearchableView.OnCategoriesSelectedListener {

    private lateinit var googleMap: GoogleMap

    private lateinit var markerCreator: MarkerCreator
    private var currentPosMarker: Marker? = null
    private var clickedMarker: Marker? = null
    private var attractionsMarkers = emptyList<Marker>()

    private val viewModel: MapViewModel by sharedViewModel()
    private val attractionsViewModel: AttractionsViewModel by sharedViewModel()

    private var requestingLocationUpdates = true
    private var showPermissionDeniedDialog = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnMyLocation.setOnClickListener {
            viewModel.receiveLocation()
        }

        markerCreator = MarkerCreator(requireActivity())

        searchView.setOnSearchTextChangeListener {
            attractionsViewModel.filterAttractionsBy(it)
        }
        searchView.onSearchResultSelected = this@MapFragment
        searchView.onCategoriesSelected = this@MapFragment
    }

    private fun handleSearchResults(event: ConsumableEvent<List<Attraction>>) {
        event.handleIfNotConsumed { attractions ->
            searchView.showSearchResults(attractions)

            true
        }
    }

    private fun hideSearch(): Boolean {
        searchView.hideSearch()

        return true
    }

    override fun onCategoriesSelected(categories: List<AttractionCategory>) {
        attractionsViewModel.categoriesSelected(categories)
    }

    override fun onSearchResultSelected(attraction: Attraction) {
        val latLng = LatLng(attraction.location.latitude, attraction.location.longitude)

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, SEARCH_RESULT_SELECTED_ZOOM_IN))
    }

    private fun handleLocationsEvent(event: ConsumableEvent<MapViewModel.Event>) {
        event.handleIfNotConsumed {
            when (it) {
                is MapViewModel.Event.LocationRequest -> {
                    btnMyLocation.visible()
                    newLocation(it.location, true)
                }
                is MapViewModel.Event.LocationUpdate -> {
                    newLocation(it.location)
                }
                is MapViewModel.Event.LocationError -> {
                    Timber.e("Location update error ${it.error}")
                }
                is MapViewModel.Event.LocationGeneralError -> {
                    showToast(getString(R.string.map_location_general_error))
                    Timber.e("Location general error")
                }
            }

            true
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap ?: return

        googleMap.setOnMapClickListener {
            hideSearch()
            uncheckClickedMarker()
        }
        googleMap.setOnCameraMoveListener {
            hideSearch()
        }
        googleMap.setOnMarkerClickListener(this)

        observe(viewModel.eventsLiveData) {
            handleLocationsEvent(it)
        }
        observe(attractionsViewModel.attractionsLiveData) {
            handleAttractions(it)
        }
        observe(attractionsViewModel.categoriesLiveData) {
            handleAttractionCategories(it)
        }
        observe(attractionsViewModel.searchResultsLiveData) {
            handleSearchResults(it)
        }
        observe(attractionsViewModel.failure, ::handleFailure)
        observe(viewModel.failure, ::handleFailure)

        attractionsViewModel.fetchAttractionsAndCategories()
        viewModel.initialize(requireContext())

        enableLocation()
    }

    private fun handleAttractionCategories(event: ConsumableEvent<List<AttractionCategory>>) {
        event.handleIfNotConsumed {
            searchView.renderCategories(it)

            true
        }
    }

    private fun handleAttractions(event: ConsumableEvent<List<Attraction>>) {
        event.handleIfNotConsumed {
            attractionsMarkers.forEach { marker ->
                marker.remove()
            }

            attractionsMarkers = googleMap.createMarkers(it, markerCreator)
            true
        }
    }

    override fun onMarkerClick(clickedMarker: Marker): Boolean {
        when (clickedMarker.tag) {
            is Attraction -> {
                createActiveIconForSelected(clickedMarker)

                centerMarkerAboveAttractionDialog(clickedMarker)

                googleMap.setOnCameraIdleListener {
                    showAttractionInfoTile(clickedMarker.tag as Attraction)
                    googleMap.setOnCameraIdleListener(null)
                }
            }
            is UserLocationMarker -> {
                googleMap.centerMarker(clickedMarker)
            }
        }

        return true
    }

    private fun createActiveIconForSelected(clickedMarker: Marker) {
        this.clickedMarker = clickedMarker
        val attraction = clickedMarker.tag as Attraction

        clickedMarker.setIcon(markerCreator.getSelectedIcon(attraction.category.id))
    }

    private fun uncheckClickedMarker() {
        clickedMarker?.let {
            val attraction = clickedMarker?.tag as Attraction
            clickedMarker?.setIcon(markerCreator.getUnselectedIcon(attraction.category.id))

            clickedMarker = null
        }
    }

    private fun showAttractionInfoTile(attraction: Attraction) {
        val image = attraction.images.first()
        val tileDialogFragment = AttractionInfoDialogFragment.newInstance(
            attraction.id,
            attraction.title,
            attraction.teaser,
            image,
            attraction.category.id,
            attraction.location.latitude,
            attraction.location.longitude
        )

        tileDialogFragment.show(fragmentManager, "fragment_attraction_tile")
        tileDialogFragment.dismissListener = {
            uncheckClickedMarker()
            attractionsViewModel.updateAttractionsOMap(attraction.id)
        }
    }

    private fun centerMarkerAboveAttractionDialog(marker: Marker) {
        val markerPoint = googleMap.projection.toScreenLocation(marker.position)

        val newYPosition = markerPoint.y + view?.height!! * ABOVE_CENTER_PERCENTAGE_POSITION

        val targetPoint = Point(markerPoint.x, newYPosition.toInt())
        val targetPosition = googleMap.projection.fromScreenLocation(targetPoint)

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), DURATION, null)
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private fun enableLocation() {
        if (activity == null)
            return

        if (EasyPermissions.hasPermissions(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {
                googleMap.isMyLocationEnabled = false
            } catch (unlikely: SecurityException) {
                Timber.e("Lost location permission. Could not request updates. $unlikely")
            }

            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isCompassEnabled = false

            if (requestingLocationUpdates) {
                viewModel.requestLocationUpdates()
            }
        } else {
            // if permissions are not currently granted, request permissions
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.map_permission_rationale_location),
                LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun newLocation(location: Location, isNeedMoveMap: Boolean = false) {
        val latLng = LatLng(location.latitude, location.longitude)

        if (currentPosMarker == null) {
            currentPosMarker = markerCreator.createPositionMarker(googleMap, latLng)
            currentPosMarker?.tag = UserLocationMarker
        }

        currentPosMarker?.let {
            markerCreator.animateMarker(googleMap, it, latLng, false)
        }

        if (isNeedMoveMap) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        showPermissionDeniedDialog = true
        btnMyLocation.invisible()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // do nothing, handled in updateMyLocation
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    /**
     * Display a dialog box asking the user to grant permissions if they were denied
     */
    override fun onResume() {
        super.onResume()
        mapView.onResume()

        if (showPermissionDeniedDialog) {
            activity?.let {
                AlertDialog.Builder(it).apply {
                    setPositiveButton(R.string.popup_text_ok, null)
                    setMessage(R.string.map_location_permission_denied)
                    create()
                }.show()
            }

            showPermissionDeniedDialog = false
        }
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        mapView.onDestroy()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions, grantResults, this
        )
    }

    object UserLocationMarker

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val SEARCH_RESULT_SELECTED_ZOOM_IN = 16f
        const val DURATION = 500
        const val DELAY = 20L
        const val ABOVE_CENTER_PERCENTAGE_POSITION = 0.3
    }
}
