package com.juandgaines.trackit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juandgaines.trackit.domain.location.LocationObserver
import com.juandgaines.trackit.domain.location.LocationTracker
import com.juandgaines.trackit.presentation.maps.MapSection
import com.juandgaines.trackit.ui.theme.TrackitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var locationTracker: LocationTracker

    private val stateChannel = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationTracker.locationData.onEach {
            Log.d("LocationData", it.toString())
        }
            .flowOn(Dispatchers.IO)
            .launchIn(lifecycleScope)

        lifecycleScope.launch {

            delay(2000)
            locationTracker.startObservingLocation()
            delay(1000)
            locationTracker.setIsTracking(true)

        }

        setContent {
            val navController = rememberNavController()
            TrackitTheme {
                Box (
                    modifier = Modifier.fillMaxSize()
                ){
                    NavHost(
                        navController = navController,
                        startDestination = MapScreenDes
                    ){
                        composable<MapScreenDes> (){
                            Scaffold {
                                MapSection(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(it)
                                )
                            }

                        }

                        composable<CameraScreenDes> {

                        }
                    }
                }

            }
        }
    }
}


@Serializable
data object MapScreenDes

@Serializable
data object CameraScreenDes