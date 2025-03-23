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

    fun onAction(action: CameraIntent) {
        viewModelScope.launch {
            when (action) {
                is CameraIntent.TakenPicture -> {

                }
                is CameraIntent.SubmitCameraPermissionInfo -> {
                    _uiState.update {
                        it.copy(
                            showCameraRationale = action.showCameraRationale,
                            permissionGranted = action.acceptedCameraPermission
                        )
                    }
                }

                CameraIntent.SavePhoto -> {

                }
                CameraIntent.CancelPreview -> {

                }
            }
        }
    }
}