package com.juandgaines.trackit.presentation.maps

sealed interface TrackingIntent {
    data object StartTracking: TrackingIntent
    data object ResumeTracking: TrackingIntent
    data object PauseTrack: TrackingIntent
}