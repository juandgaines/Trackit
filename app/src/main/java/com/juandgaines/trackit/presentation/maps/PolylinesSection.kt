package com.juandgaines.trackit.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun PolylinesSections(){

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