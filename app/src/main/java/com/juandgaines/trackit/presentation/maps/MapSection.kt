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
import com.google.maps.android.compose.rememberUpdatedMarkerState

val latLngArray =  listOf(
    LatLng(4.6547591408952185, -74.05578687079682),
    LatLng(4.656732, -74.057851),
    LatLng(4.668311, -74.074094),
)

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapSection(
    modifier:Modifier = Modifier
){
    val activity = LocalActivity.current as ComponentActivity

    val marker = rememberUpdatedMarkerState()



    LaunchedEffect(true) {

        marker.position = LatLng(
            4.6547591408952185, -74.05578687079682
        )
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

        MapEffect(latLngArray) { map ->

            val boundariesBuilder = LatLngBounds.builder()
            latLngArray.forEach {
                boundariesBuilder.include(it)
            }

            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundariesBuilder.build(),
                    100
                )
            )

        }

        MarkerComposable (
            state = marker
        ){
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