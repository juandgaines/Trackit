package com.juandgaines.trackit.data.di

import android.content.Context
import com.juandgaines.trackit.data.AndroidLocationObserver
import com.juandgaines.trackit.domain.location.LocationObserver
import com.juandgaines.trackit.domain.location.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideLocationObserver(
        @ApplicationContext
        context: Context
    ): LocationObserver {
        return AndroidLocationObserver(context)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        locationObserver: LocationObserver,
        coroutineScope: CoroutineScope
    ): LocationTracker {
        return LocationTracker(
            locationObserver,
            coroutineScope
        )
    }
}