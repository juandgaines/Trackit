package com.juandgaines.trackit.presentation.maps

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdate
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
){
    val activity = LocalActivity.current as ComponentActivity

    val cameraPositionState = rememberCameraPositionState()
    val marker = rememberUpdatedMarkerState()


    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.long?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    val markerPosition = remember(markerPositionLat, markerPositionLong) {
        LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
    }


    LaunchedEffect(markerPosition, isTrackingFinished) {
        if(!isTrackingFinished) {
            marker.position = markerPosition
        }
    }

    LaunchedEffect(currentLocation, isTrackingFinished) {
        if(currentLocation != null && !isTrackingFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }

    GoogleMap(
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

        PolylinesSections()

        MapEffect(locations) { map ->


            val boundariesBuilder = LatLngBounds.builder()
            locations.flatten().forEach { location ->
                boundariesBuilder
                    .include(
                        LatLng(
                            location.location.lat,
                            location.location.long,
                        )
                    )
            }
            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundariesBuilder.build(),
                    100
                )
            )
        }

        if(!isTrackingFinished && currentLocation != null) {
            MarkerComposable(
                state = marker
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
    }
}