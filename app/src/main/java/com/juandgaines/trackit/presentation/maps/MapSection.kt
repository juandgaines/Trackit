package com.juandgaines.trackit.presentation.maps

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.juandgaines.trackit.domain.location.Location
import com.juandgaines.trackit.domain.location.LocationWithTimestamp


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapSection(
    modifier:Modifier = Modifier,
    currentLocation: Location?,
    isTrackingFinished: Boolean,
    locations: List<List<LocationWithTimestamp>>,
    selectedLocation: LocationWithTimestamp? = null,
    onAction: (TrackingIntent) -> Unit
){
    val activity = LocalActivity.current as ComponentActivity

    val cameraPositionState = rememberCameraPositionState()

    val markerPosition = remember(currentLocation) {
        LatLng(
            (currentLocation?.lat?.toFloat() ?: 0f).toDouble(),
            (currentLocation?.long?.toFloat() ?: 0f).toDouble())
    }

    val marker = rememberUpdatedMarkerState(markerPosition)


    LaunchedEffect(currentLocation, isTrackingFinished) {
        if(currentLocation != null && !isTrackingFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }

    selectedLocation?.let {
        PhotoGalleryDialog(
            locationWithPhotos = selectedLocation,
            onDismiss = {
                onAction(TrackingIntent.DismissDialogLocation)
                onAction(TrackingIntent.ResumeTracking)
            }
        )
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = modifier,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
        ),
        onMapLoaded = {
            Toast.makeText(activity,"Mapa cargado",Toast.LENGTH_SHORT).show()
        },
        properties = MapProperties(
            isTrafficEnabled = true,
            isIndoorEnabled = true,
        )
    ){

        PolylinesSections(locations)

        MapEffect(locations) { map ->

        }

        if(!isTrackingFinished && currentLocation != null) {
            MarkerComposable(
                state = marker,
                onClick = {
                    onAction(TrackingIntent.GoToCamera)
                    true
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        ClusterSection(
            locations = locations,
            onAction = onAction
        )
    }
}