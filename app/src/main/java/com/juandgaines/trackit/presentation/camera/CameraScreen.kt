package com.juandgaines.trackit.presentation.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.juandgaines.trackit.R
import com.juandgaines.trackit.presentation.dialogs.PermissionRationaleDialogs
import com.juandgaines.trackit.presentation.utils.hasCameraPermission
import com.juandgaines.trackit.presentation.utils.hasLocationPermission
import com.juandgaines.trackit.presentation.utils.shouldShowCameraPermissionRationale

@Composable
fun CameraScreenRoot() {

    val viewModel: CameraViewModel = hiltViewModel()

    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()
    val previewPhoto by viewModel.previewPhoto.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity
    val permissionLauncherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    )
    { _->

        val cameraRationale = activity.shouldShowCameraPermissionRationale()

        viewModel.onAction(
            CameraIntent.SubmitCameraPermissionInfo(
                acceptedCameraPermission = activity.hasCameraPermission(),
                showCameraRationale = cameraRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        val showCameraRationale = activity.shouldShowCameraPermissionRationale()

        viewModel.onAction(
            CameraIntent.SubmitCameraPermissionInfo(
                acceptedCameraPermission = context.hasLocationPermission(),
                showCameraRationale = showCameraRationale
            )
        )

        if (!showCameraRationale) {
            permissionLauncherCamera.requestCameraScreenPermissions(context)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ){

        when{
            uiState.isInPreviewMode && previewPhoto != null->{
                PhotoPreviewScreen(
                    photoBytes = previewPhoto!!,
                    onSave = { viewModel.onAction(
                        CameraIntent.SavePhoto
                    ) },
                    onCancel = { viewModel.onAction(
                        CameraIntent.CancelPreview
                    ) }
                )
            }
            uiState.permissionGranted ->{
                CameraScreen(
                    onPhotoTaken = { imageProxy ->
                        val matrix = Matrix().apply {
                            postRotate(
                                imageProxy.imageInfo.rotationDegrees.toFloat()
                            )
                        }
                        val rotatedBitmap = imageProxy.toBitmap().let {
                            Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
                        }
                        viewModel.onAction(
                            CameraIntent.TakenPicture(
                                rotatedBitmap.toByteArray()
                            )
                        )
                    }
                )
            }
            else->{
                Text(
                    text = stringResource(id = R.string.camera_permission_denied),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
    PermissionRationaleDialogs(
        showNotificationRationale = false,
        showLocationRationale = false,
        showCameraRationale = uiState.showCameraRationale,
        onAccept = {
            permissionLauncherCamera.requestCameraScreenPermissions(context)
        },
        onDismiss = {
            viewModel.onAction(
                CameraIntent.SubmitCameraPermissionInfo(
                    acceptedCameraPermission = activity.hasCameraPermission(),
                    showCameraRationale = false
                )
            )
        }
    )

}

@Composable
fun PhotoPreviewScreen(
    photoBytes: ByteArray,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Display the photo
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                bitmap = photoBytes.toBitmap().asImageBitmap(),
                contentDescription = "Photo preview",
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancel"
                )
            }
            
            IconButton(
                onClick = onSave,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save"
                )
            }
        }
    }
}

@Composable
fun CameraScreen(
    onPhotoTaken: (ImageProxy) -> Unit
) {
    val localContext = LocalContext.current
    
    val controller = remember {
        LifecycleCameraController(localContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = {
                controller.cameraSelector = if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            },
            modifier = Modifier.offset(16.dp, 36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Cameraswitch,
                contentDescription = "Switch camera"
            )
        }

        IconButton(
            onClick = {
                takePhoto(
                    context = localContext,
                    controller = controller,
                    onPhotoTaken = onPhotoTaken
                )
            },
            modifier = Modifier.align(
                alignment = Alignment.BottomCenter
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Take photo"
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (ImageProxy) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                onPhotoTaken(image)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraScreen", "Error taking picture", exception)
            }
        }
    )
}

private fun ManagedActivityResultLauncher<String, Boolean>.requestCameraScreenPermissions(
    context: Context
) {
    val hasCameraPermission = context.hasCameraPermission()
    if (!hasCameraPermission) {
        launch(Manifest.permission.CAMERA)
    }
}

