package com.juandgaines.trackit.domain.location

import kotlin.math.roundToInt


object LocationCalculations {

    fun getTotalDistanceMeters(locations: List<List<LocationWithTimestamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { location1, location2 ->
                    location1.location.distanceTo(location2.location)
                }.sum().roundToInt()
            }
    }
}