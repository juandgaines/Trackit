package com.juandgaines.trackit.presentation.maps

import com.juandgaines.trackit.domain.location.Location
import com.juandgaines.trackit.domain.location.LocationWithTimestamp

data class TrackLocationState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val location: Location? = null,
    val selectedLocation: LocationWithTimestamp? = null,
    val trackingDataSegments:List<List<LocationWithTimestamp>> = emptyList(),
    val showLocationRationale: Boolean = false,
    val showNotificationRationale: Boolean = false,
)