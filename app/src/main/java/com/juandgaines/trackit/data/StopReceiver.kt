package com.juandgaines.trackit.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.juandgaines.trackit.TrackitService
import com.juandgaines.trackit.domain.camera.PhotoHandler
import com.juandgaines.trackit.domain.location.LocationTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StopReceiver : BroadcastReceiver (){

    @Inject
    lateinit var photoHandler: PhotoHandler
    @Inject
    lateinit var locationTracker: LocationTracker

    @Inject
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context?, intent: Intent?) {

        val receiverAction = intent?.action ?: return

        if (receiverAction != ACTION_STOP)
            return
        else{
            applicationScope.launch {
                applicationScope.launch {
                    locationTracker.setIsTracking(false)
                    locationTracker.finishTracking()
                    photoHandler.clearPhotos()
                }.join()
            }

            val serviceIntent = Intent(context, TrackitService::class.java).apply {
                action = TrackitService.ACTION_STOP
            }
            if (TrackitService.isServiceActive.value) {
                context?.startService(serviceIntent)
            }
        }

    }

    companion object{
        const val ACTION_STOP = "com.juandgaines.trackit.action.STOP_TRACKING"
    }
}