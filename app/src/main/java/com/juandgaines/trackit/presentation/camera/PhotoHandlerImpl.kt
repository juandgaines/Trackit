package com.juandgaines.trackit.presentation.camera

import android.content.Context
import com.juandgaines.trackit.domain.camera.PhotoHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PhotoHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoHandler {

    private val photosDirectory: File by lazy {
        File(context.filesDir, "photos").apply {
            if (!exists()) mkdirs()
        }
    }
    
    private val _currentPreviewPhoto = MutableStateFlow<ByteArray?>(null)
    private val _photos = MutableStateFlow<List<File>>(emptyList())
    
    init {
        // Initialize photos list
        loadSavedPhotos()
    }
    
    private fun loadSavedPhotos() {
        if (photosDirectory.exists()) {
            _photos.value = photosDirectory.listFiles()
                ?.filter { it.isFile && it.extension.lowercase() in listOf("jpg", "jpeg", "png") }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        }
    }

    override suspend fun onPhotoForPreview(photoBytes: ByteArray) {
        _currentPreviewPhoto.value = photoBytes
    }

    override suspend fun savePicturePreview(): File? = withContext(Dispatchers.IO) {
        val photoBytes = _currentPreviewPhoto.value ?: return@withContext null
        
        try {
            // Create filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = File(photosDirectory, "PHOTO_$timestamp.jpg")
            
            // Write bytes to file
            FileOutputStream(photoFile).use { outputStream ->
                outputStream.write(photoBytes)
                outputStream.flush()
            }
            
            // Update photos list
            loadSavedPhotos()
            
            // Clear preview
            _currentPreviewPhoto.value = null
            
            photoFile
        } catch (e: Exception) {
            ensureActive()
            e.printStackTrace()
            null
        }
    }

    override suspend fun onCancelPicturePreview() {
        _currentPreviewPhoto.value = null
    }

    override fun getPhotos(): Flow<List<File>> = _photos.asStateFlow()

    override fun getCurrentPreviewPhoto(): Flow<ByteArray?> = _currentPreviewPhoto.asStateFlow()

    override suspend fun clearPhotos() = withContext(Dispatchers.IO) {
        photosDirectory.listFiles()?.forEach { it.delete() }
        loadSavedPhotos()
    }
} 