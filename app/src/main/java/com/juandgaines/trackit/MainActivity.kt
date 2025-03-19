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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.maps.android.compose.GoogleMap
import com.juandgaines.trackit.domain.timer.Timer
import com.juandgaines.trackit.presentation.maps.MapSection
import com.juandgaines.trackit.ui.theme.TrackitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stateChannel = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            var counter:Duration = Duration.ZERO

            Timer.timeAndEmits().collect{
                counter+=it
                Log.d("FlowTimer", "Time interval ${counter.inWholeSeconds} s")
            }
        }

        Timer.randomFlow().onEach {
            Log.d("FlowRandom", "Random number $it")
        }.launchIn(lifecycleScope)

        Timer.timeAndEmits()
            .scan(Duration.ZERO){ acc, value ->
                acc + value
            }
            .combine(
                Timer.randomFlow()
            ){ time, random ->
                time to random
            }.onEach {
                Log.d("FlowWithCombine", "Time interval ${it.first.inWholeSeconds} s, Random number ${it.second}")
            }.launchIn(lifecycleScope)


        Timer.timeAndEmits()
            .scan(Duration.ZERO){ acc, value ->
                acc + value
            }
            .zip(
                Timer.randomFlow()
            ){ time, random ->
                time to random
            }.onEach {
                Log.d("FlowWithZip", "Time interval ${it.first.inWholeSeconds} s, Random number ${it.second}")
            }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            Timer.counterFlow()
                .conflate()
                .collect{
                    kotlinx.coroutines.delay(700)
                    Log.d("FlowWithBackPressure", "Time interval ${it} s")
                }
            }


        stateChannel.flatMapLatest{ channel->
            if (channel){
                Timer.timeAndEmits().scan(Duration.ZERO){acc, value ->
                    acc + value
                }
            }
            else{
                Timer.randomFlow()
            }

        }.onEach {
            if(stateChannel.value)
                Log.d("FlowWithFlatMapLatest", "Time interval ${(it as Duration).inWholeSeconds} s")
            else
                Log.d("FlowWithFlatMapLatest", "Random number ${(it as Int)} s")

        }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            kotlinx.coroutines.delay(10000L)
            stateChannel.value = true
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