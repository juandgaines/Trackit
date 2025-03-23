package com.juandgaines.trackit.domain.camera

import kotlinx.coroutines.flow.Flow
import java.io.File

interface PhotoHandler {

    suspend fun onPhotoForPreview(photoBytes: ByteArray)

    suspend fun savePicturePreview(): File?

    suspend fun onCancelPicturePreview()

    fun getPhotos(): Flow<List<File>>

    fun getCurrentPreviewPhoto(): Flow<ByteArray?>

    suspend fun clearPhotos()
}