package com.nicklasslagbrand.placeholder.extension

import android.graphics.Point
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.feature.main.map.MapFragment
import com.nicklasslagbrand.placeholder.feature.main.map.MarkerCreator

fun GoogleMap.createMarkers(attractions: List<Attraction>, markerCreator: MarkerCreator): List<Marker> {
    return attractions.map {
        val marker = addMarker(markerCreator.buildMarker(it.category.id, it.location))
        marker.tag = it

        marker
    }
}

fun GoogleMap.centerMarker(marker: Marker) {
    val markerPoint = projection.toScreenLocation(marker.position)
    val targetPoint = Point(markerPoint.x, markerPoint.y)
    val targetPosition = projection.fromScreenLocation(targetPoint)

    animateCamera(CameraUpdateFactory.newLatLng(targetPosition), MapFragment.DURATION, null)
}
