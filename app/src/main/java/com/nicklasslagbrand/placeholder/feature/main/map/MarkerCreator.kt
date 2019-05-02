package com.nicklasslagbrand.placeholder.feature.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.SystemClock
import android.util.SparseArray
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Location
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator.MarkerType.ART
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator.MarkerType.FUN
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator.MarkerType.HISTORY
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator.MarkerType.ROYAL
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator.MarkerType.SIGHT

class MarkerCreator(val context: Context) {
    private var iconCache = SparseArray<BitmapDescriptor>()

    init {
        iconCache.put(ART.resActive, getIcon(ART, true))
        iconCache.put(ART.resDisable, getIcon(ART, false))
        iconCache.put(FUN.resActive, getIcon(FUN, true))
        iconCache.put(FUN.resDisable, getIcon(FUN, false))
        iconCache.put(HISTORY.resActive, getIcon(HISTORY, true))
        iconCache.put(HISTORY.resDisable, getIcon(HISTORY, false))
        iconCache.put(ROYAL.resActive, getIcon(ROYAL, true))
        iconCache.put(ROYAL.resDisable, getIcon(ROYAL, false))
        iconCache.put(SIGHT.resActive, getIcon(SIGHT, true))
        iconCache.put(SIGHT.resDisable, getIcon(SIGHT, false))
    }

    fun buildMarker(categoryId: Int, location: Location, isActive: Boolean = true): MarkerOptions {
        val latLng = LatLng(location.latitude, location.longitude)

        return MarkerOptions()
            .anchor(THRESHOLD_IMAGE_5F, THRESHOLD_IMAGE_5F)
            .position(latLng)
            .icon(getCachedIcon(getMarkerTypeForCategoryId(categoryId), isActive))
    }

    fun getSelectedIcon(typeId: Int): BitmapDescriptor {
        return getCachedIcon(getMarkerTypeForCategoryId(typeId), false)
    }

    fun getUnselectedIcon(typeId: Int): BitmapDescriptor {
        return getCachedIcon(getMarkerTypeForCategoryId(typeId), true)
    }

    private fun getCachedIcon(type: MarkerType, isActive: Boolean): BitmapDescriptor {
        val resKey = if (isActive) type.resActive else type.resDisable

        return iconCache[resKey]
    }

    private fun getIcon(type: MarkerType, isActive: Boolean): BitmapDescriptor {
        val multiplier = if (isActive)
            MARKER_DEFAULT_MULTIPLIER else MARKER_SELECTED_MULTIPLIER

        val width = (WIDTH_POSITION_MARKER * getDpi() * multiplier).toInt()
        val height = (HEIGHT_POSITION_MARKER * getDpi() * multiplier).toInt()

        return scaleBitmap(width, height, type.resActive)
    }

    private fun getDpi(): Float {
        return context.resources.displayMetrics.density
    }

    private fun scaleBitmap(width: Int, height: Int, res: Int): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(context.resources, res)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private fun getMarkerTypeForCategoryId(id: Int): MarkerType {
        return when (id) {
            ART.id -> ART
            FUN.id -> FUN
            HISTORY.id -> HISTORY
            ROYAL.id -> ROYAL
            SIGHT.id -> SIGHT
            else -> SIGHT
        }
    }

    fun createPositionMarker(googleMap: GoogleMap, latLng: LatLng): Marker {
        val width = (WIDTH_POSITION_MARKER * getDpi()).toInt()
        val height = (WIDTH_POSITION_MARKER * getDpi()).toInt()

        return googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .anchor(THRESHOLD_IMAGE_5F, THRESHOLD_IMAGE_4F)
                .icon(scaleBitmap(width, height, R.drawable.ic_loc_current_position))
        )
    }

    fun animateMarker(googleMap: GoogleMap, marker: Marker, toPosition: LatLng, hideMarker: Boolean) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val startPoint = googleMap.projection.toScreenLocation(marker.position)
        val startLatLng = googleMap.projection.fromScreenLocation(startPoint)

        val interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / MapFragment.DURATION)
                val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude
                marker.position = LatLng(lat, lng)

                if (t < 1.0) {
                    handler.postDelayed(this, MapFragment.DELAY)
                } else {
                    marker.isVisible = !hideMarker
                }
            }
        })
    }

    enum class MarkerType(val id: Int, val resActive: Int, val resDisable: Int) {
        ART(Attraction.ART_CATEGORY_ID, R.drawable.ic_art_a, R.drawable.ic_art_d),
        FUN(Attraction.FUN_CATEGORY_ID, R.drawable.ic_fun_a, R.drawable.ic_fun_d),
        HISTORY(Attraction.HISTORY_CATEGORY_ID, R.drawable.ic_history_a, R.drawable.ic_history_d),
        ROYAL(Attraction.ROYAL_CATEGORY_ID, R.drawable.ic_royal_a, R.drawable.ic_royal_d),
        SIGHT(Attraction.SIGHT_CATEGORY_ID, R.drawable.ic_sight_a, R.drawable.ic_sight_d),
    }

    companion object {
        const val MARKER_DEFAULT_MULTIPLIER = 1.0
        const val MARKER_SELECTED_MULTIPLIER = 1.2
        const val THRESHOLD_IMAGE_5F = .5f
        const val THRESHOLD_IMAGE_4F = .5f
        const val WIDTH_POSITION_MARKER = 40
        const val HEIGHT_POSITION_MARKER = 48
    }
}
