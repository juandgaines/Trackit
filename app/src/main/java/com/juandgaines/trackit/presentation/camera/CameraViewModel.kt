package com.juandgaines.trackit.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.trackit.domain.camera.PhotoHandler
import com.juandgaines.trackit.domain.location.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor (
    private val photoHandler: PhotoHandler,
    private val locationTracker: LocationTracker
): ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    val previewPhoto: StateFlow<ByteArray?> = photoHandler.getCurrentPreviewPhoto()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onAction(action:CameraIntent){
        viewModelScope.launch {
            when(action){
                is CameraIntent.TakenPicture -> processPhoto(action.data)
                is CameraIntent.SubmitCameraPermissionInfo -> {
                    _uiState.update {
                        it.copy(
                            showCameraRationale = action.showCameraRationale,
                            permissionGranted = action.acceptedCameraPermission
                        )
                    }
                }

                CameraIntent.SavePhoto -> savePhoto()
                CameraIntent.CancelPreview -> cancelPreview()
            }
        }
    }


    /**
     * Process a new photo from camera
     */
    private suspend fun processPhoto(photoBytes: ByteArray) {
        photoHandler.onPhotoForPreview(photoBytes)
        _uiState.value = _uiState.value.copy(isInPreviewMode = true)
    }

    /**
     * Save the current preview photo and associate it with the latest location
     */

    private suspend fun savePhoto() {
        val savedFile = photoHandler.savePicturePreview()
        // If we have a saved file, associate it with the latest location
        if (savedFile != null) {
            associatePhotoWithLatestLocation(savedFile)
        }
        _uiState.value = _uiState.value.copy(
            isInPreviewMode = false,
            lastSavedPhoto = savedFile
        )
    }

    private suspend  fun associatePhotoWithLatestLocation(photoFile: File) {
        val locationData = locationTracker.locationData.value

        // Find the last non-empty segment (if any)
        val lastNonEmptySegmentIndex = locationData.locations.indexOfLast { it.isNotEmpty() }

        // If no non-empty segments found, we can't associate the photo
        if (lastNonEmptySegmentIndex == -1) return

        // Get the last non-empty segment
        val lastNonEmptySegment = locationData.locations[lastNonEmptySegmentIndex]

        // Get the latest location in that segment
        val latestLocation = lastNonEmptySegment.last()

        // Create a new location with the photo added to its list
        val updatedLocation = latestLocation.copy(
            listPhotos = latestLocation.listPhotos + photoFile
        )

        // Replace the latest location in the segment with our updated one
        val updatedSegment = lastNonEmptySegment.dropLast(1) + updatedLocation

        // Create a new list of segments with the updated segment
        val updatedSegments = locationData.locations.toMutableList().apply {
            set(lastNonEmptySegmentIndex, updatedSegment)
        }

        // Update the location data
        locationTracker.updateLocationData(
            locationData.copy(locations = updatedSegments)
        )
    }

    private suspend fun cancelPreview() {
        photoHandler.onCancelPicturePreview()
        _uiState.value = _uiState.value.copy(isInPreviewMode = false)
    }
}