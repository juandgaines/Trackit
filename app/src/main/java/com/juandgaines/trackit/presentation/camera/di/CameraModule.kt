package com.juandgaines.trackit.presentation.camera.di

import android.content.Context
import com.juandgaines.trackit.domain.camera.PhotoHandler
import com.juandgaines.trackit.presentation.camera.PhotoHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CameraModule {

    @Provides
    @Singleton
    fun providePhotoHandler(
        @ApplicationContext context: Context,
    ): PhotoHandler {
        return PhotoHandlerImpl(context)
    }
}