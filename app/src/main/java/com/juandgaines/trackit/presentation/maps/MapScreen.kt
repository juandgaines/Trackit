package com.juandgaines.trackit.presentation.maps

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.unit.dp


@Composable
fun MapScreenRoot(
    trackingViewModel: TrackingMapViewModel,
    navigateToCameraScreen: () -> Unit
){
    val state by trackingViewModel.state.collectAsState()

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
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}