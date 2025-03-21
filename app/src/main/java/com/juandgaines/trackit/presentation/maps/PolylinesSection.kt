package com.juandgaines.trackit.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.juandgaines.trackit.domain.location.LocationWithTimestamp

@Composable
fun PolylinesSections(
    locations: List<List<LocationWithTimestamp>>,
){

    val polylines = remember (locations){
        locations.map { location ->
            location.zipWithNext{ timestamp1, timestamp2 ->
                PolyLineUi(
                    location1 = timestamp1.location,
                    location2 = timestamp2.location,
                    color = Color.Blue
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { polylineUi ->
            Polyline(
                points = listOf(
                    LatLng(polylineUi.location1.lat, polylineUi.location1.long),
                    LatLng(polylineUi.location2.lat, polylineUi.location2.long),
                ),
                color = polylineUi.color,
                jointType = JointType.BEVEL
            )
        }
    }
}