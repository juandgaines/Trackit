package com.juandgaines.trackit.data.di

import android.content.Context
import com.juandgaines.trackit.data.AndroidLocationObserver
import com.juandgaines.trackit.domain.location.LocationObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}