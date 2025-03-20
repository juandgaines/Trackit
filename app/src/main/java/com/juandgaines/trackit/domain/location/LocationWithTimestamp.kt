package com.juandgaines.trackit.domain.location

import java.io.File
import kotlin.time.Duration

data class LocationWithTimestamp(
    val location: Location,
    val timestamp: Duration,
    val listPhotos: List<File> = emptyList()
)