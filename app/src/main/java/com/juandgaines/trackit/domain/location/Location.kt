package com.juandgaines.trackit.domain.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(
    val lat: Double,  // Latitude in degrees
    val long: Double, // Longitude in degrees
) {

    fun distanceTo(other: Location): Float {
        val latDistance = Math.toRadians(other.lat - lat)
        val longDistance = Math.toRadians(other.long - long)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(lat)) * cos(Math.toRadians(other.lat)) *
            sin(longDistance / 2) * sin(longDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c.toFloat()
    }



    companion object {
        // Earth's radius in meters, used for distance calculations
        private const val EARTH_RADIUS_METERS = 6_371_000
    }
}