package com.juandgaines.trackit.presentation.maps

import androidx.compose.ui.graphics.Color
import com.juandgaines.trackit.domain.location.Location

data class PolyLineUi(
    val location1: Location,
    val location2: Location,
    val color: Color
)