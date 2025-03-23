package com.juandgaines.trackit.presentation.camera

sealed interface CameraIntent {
    data class TakenPicture(val data:ByteArray) : CameraIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TakenPicture

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    data class SubmitCameraPermissionInfo(
        val acceptedCameraPermission: Boolean,
        val showCameraRationale: Boolean
    ) : CameraIntent

    data object SavePhoto: CameraIntent
    data object CancelPreview: CameraIntent
}