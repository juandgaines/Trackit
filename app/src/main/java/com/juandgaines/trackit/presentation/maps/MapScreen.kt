package com.juandgaines.trackit.presentation.maps

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.juandgaines.trackit.presentation.dialogs.PermissionRationaleDialogs
import com.juandgaines.trackit.presentation.utils.hasLocationPermission
import com.juandgaines.trackit.presentation.utils.hasNotificationPermission
import com.juandgaines.trackit.presentation.utils.shouldShowLocationPermissionRationale
import com.juandgaines.trackit.presentation.utils.shouldShowPostNotificationPermissionRationale


@Composable
fun MapScreenRoot(
    trackingViewModel: TrackingMapViewModel,
    navigateToCameraScreen: () -> Unit
){
    val state by trackingViewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        trackingViewModel.events.collect{ event->
            when(event){
                is TrackingEvents.NavigateToCamera -> navigateToCameraScreen()
            }
        }
    }

    MapScreen(
        state = state,
        onAction = trackingViewModel::onAction
    )
}

@Composable
fun MapScreen(
    onAction: (TrackingIntent) -> Unit,
    state: TrackLocationState
) {

    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity

    val permissionLauncherLocationAndNotifications = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val hasCourseLocationPermission = activity.hasLocationPermission()
        val hasNotificationPermission = activity.hasNotificationPermission()

        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowPostNotificationPermissionRationale()

        onAction(
            TrackingIntent.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            TrackingIntent.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }
    PermissionRationaleDialogs(
        showNotificationRationale = state.showNotificationRationale,
        showLocationRationale = state.showLocationRationale,
        showCameraRationale = false,
        onAccept = {
            permissionLauncherLocationAndNotifications.requestTrackingScreenPermissions(context)
        },
        onDismiss = {
            onAction(TrackingIntent.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = false
            ))
            onAction(TrackingIntent.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = false
            ))
        }
    )

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowPostNotificationPermissionRationale()

        onAction(
            TrackingIntent.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            TrackingIntent.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showLocationRationale && !showNotificationRationale) {
            permissionLauncherLocationAndNotifications.requestTrackingScreenPermissions(context)
        }

        if (context.hasLocationPermission()) {
            onAction(TrackingIntent.StartTracking)
            onAction(TrackingIntent.ResumeTracking)
        }

    }

    Scaffold (
        floatingActionButtonPosition =  FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when{
                        state.isPaused->{
                            onAction(TrackingIntent.ResumeTracking)
                        }
                        else->{
                            onAction(TrackingIntent.PauseTrack)
                        }
                    }
                }
            ) {
                if (state.isPaused ) {
                    Image(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )

                } else {
                    Image(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    ){ padding->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            MapSection(
                currentLocation = state.location,
                isTrackingFinished = false,
                locations = state.trackingDataSegments,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

private fun ActivityResultLauncher<Array<String>>.requestTrackingScreenPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val notificationPermission = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }

        !hasLocationPermission -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermission)
    }
}