package com.juandgaines.trackit.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun PolylinesSections(){

    val latLngArray =  listOf(
        LatLng(4.6547591408952185, -74.05578687079682),
        LatLng(4.656732, -74.057851),
        LatLng(4.668311, -74.074094),
    )
    val newArray = latLngArray.zipWithNext()

    newArray.forEach { pair ->
        Polyline(
            points = listOf(
                pair.first,
                pair.second,
            ),
            color = Color.Blue,
            jointType = JointType.BEVEL
        )
    }
}