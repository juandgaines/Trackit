package com.juandgaines.trackit.domain.location

import com.juandgaines.trackit.domain.timer.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import kotlin.time.Duration


class LocationTracker @Inject constructor(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope,
) {

    private val _locationData = MutableStateFlow(LocationData())
    val locationData = _locationData.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()



    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }

    fun finishTracking() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        _locationData.value = LocationData()
    }

    fun updateLocationData(newLocationData: LocationData) {
        _locationData.value = newLocationData
    }

    val currentLocation = isObservingLocation

        .flatMapLatest { isObservingLocation ->
            if(isObservingLocation) {
                locationObserver.observeLocation(1000L)
            } else {
                flowOf()
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )


    init {
        _isTracking
            .onEach { isTracking ->
                if(!isTracking) {
                    val newList = buildList {
                        addAll(locationData.value.locations)
                        add(emptyList<LocationWithTimestamp>())
                    }.toList()
                    _locationData.update { it.copy(
                        locations = newList
                    ) }
                }
            }
            .flatMapLatest { isTracking ->
                if(isTracking) {
                    Timer.timeAndEmits()
                } else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)


        locationData
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTracking) { location, isTracking ->
                if(isTracking) {
                    emit(location)
                }
            }
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationWithTimestamp(
                    location = location,
                    timestamp = elapsedTime
                )
            }
            .onEach { location ->

                val currentLocations = locationData.value.locations
                val lastLocationsList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + location
                } else {
                    listOf(location)
                }

                val newLocationsList = currentLocations.replaceLast(lastLocationsList.distinct())

                val distanceMeters = LocationCalculations.getTotalDistanceMeters(
                    locations = newLocationsList
                )

                _locationData.update {
                    LocationData(
                        distanceMeters = distanceMeters,
                        locations = newLocationsList,
                    )
                }
            }
            .launchIn(applicationScope)


    }


}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if(this.isEmpty()) {
        return listOf(replacement)
    }
    return this.dropLast(1) + listOf(replacement)
}