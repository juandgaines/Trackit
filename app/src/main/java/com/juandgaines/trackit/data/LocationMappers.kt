package com.juandgaines.trackit.data

import android.location.Location

fun Location.toLocation():com.juandgaines.trackit.domain.location.Location{
    return com.juandgaines.trackit.domain.location.Location(
        lat = latitude,
        long = longitude
    )
}