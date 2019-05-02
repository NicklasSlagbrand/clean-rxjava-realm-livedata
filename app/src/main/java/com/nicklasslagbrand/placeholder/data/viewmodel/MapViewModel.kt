package com.nicklasslagbrand.placeholder.data.viewmodel

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber

class MapViewModel : RxBaseViewModel() {
    val eventsLiveData = MutableLiveData<ConsumableEvent<Event>>()
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCleared() {
        super.onCleared()

        stopLocationUpdates()
    }

    fun initialize(requireContext: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    for (location in locationResult.locations) {
                        eventsLiveData.value = ConsumableEvent(Event.LocationUpdate(location))
                    }
                }
            }
        }
    }

    fun receiveLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location == null) {
                        eventsLiveData.value = ConsumableEvent(Event.LocationGeneralError)
                    } else {
                        eventsLiveData.value = ConsumableEvent(Event.LocationRequest(location))
                    }
                }
                .addOnFailureListener {
                    eventsLiveData.value = ConsumableEvent(Event.LocationError(it.localizedMessage))
                }
        } catch (unlikely: SecurityException) {
            eventsLiveData.value = ConsumableEvent(Event.LocationError(unlikely.localizedMessage))
        }
    }

    fun requestLocationUpdates() {
        val locationRequest = LocationRequest()

        with(locationRequest) {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Timber.e("Lost location permission. Could not request updates. $unlikely")
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    sealed class Event {
        data class LocationRequest(val location: Location) : Event()
        data class LocationUpdate(val location: Location) : Event()
        data class LocationError(val error: String) : Event()
        object LocationGeneralError : Event()
    }

    companion object {
        const val UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000L // 10 seconds
        const val FASTEST_INTERVAL_IN_MILLISECONDS = 5 * 1000L // 5 seconds
    }
}
