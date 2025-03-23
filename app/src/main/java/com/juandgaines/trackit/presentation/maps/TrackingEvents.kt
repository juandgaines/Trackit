package com.juandgaines.trackit.presentation.maps

sealed interface TrackingEvents{
    data object NavigateToCamera: TrackingEvents
}