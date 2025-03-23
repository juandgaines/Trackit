@file:Suppress("DEPRECATION")

package com.juandgaines.trackit.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import com.juandgaines.trackit.domain.location.LocationWithTimestamp

@Composable
fun ClusterSection(
    modifier: Modifier = Modifier,
    locations: List<List<LocationWithTimestamp>>,
    onAction:(TrackingIntent) -> Unit
) {
    val listWithPictures = remember (locations){
        locations.flatten().filter { it.listPhotos.isNotEmpty() }
    }

    listWithPictures.forEach { locationWithTimestamp ->
        val latLng = LatLng(
            locationWithTimestamp.location.lat,
            locationWithTimestamp.location.long
        )

        val photoMarkerState = rememberMarkerState(
            position = latLng
        )

        MarkerComposable (
            state = photoMarkerState,
            onClick = {
                onAction(TrackingIntent.PauseTrack)
                onAction(TrackingIntent.SelectLocation(locationWithTimestamp))
                true
            }
        ){

            PhotoCluster(
                locationWithPhotos = locationWithTimestamp,
            )
        }
    }
}