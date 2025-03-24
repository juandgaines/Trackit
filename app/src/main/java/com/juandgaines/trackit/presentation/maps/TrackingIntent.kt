package com.juandgaines.trackit.presentation.maps

import com.juandgaines.trackit.domain.location.LocationWithTimestamp

sealed interface TrackingIntent {

    data class SubmitLocationPermissionInfo(
        val  acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean,
    ) : TrackingIntent

    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationRationale: Boolean,
    ): TrackingIntent

    data object StartTracking: TrackingIntent
    data object ResumeTracking: TrackingIntent
    data object PauseTrack: TrackingIntent

    data object GoToCamera: TrackingIntent

    data class SelectLocation(val location: LocationWithTimestamp): TrackingIntent
    data object DismissDialogLocation: TrackingIntent
}