package com.juandgaines

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class TrackitApplication:Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
}