package com.juandgaines.trackit

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.juandgaines.trackit.domain.location.LocationTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TrackitService: Service() {


    @Inject
    lateinit var locationTracker: LocationTracker

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                start()
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }
    private fun start(){
        if (!_isServiceActive.value){
            _isServiceActive.value = true
            locationTracker.elapsedTime.onEach { elapsedTime ->
                Log.d("Service", "elapsedTime: $elapsedTime")

            }.launchIn(serviceScope)
        }
    }


    fun stop () {
        stopSelf()
        _isServiceActive.value = false
        serviceScope.cancel()
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        private val _isServiceActive = MutableStateFlow(false)
        var isServiceActive = _isServiceActive.asStateFlow()
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}