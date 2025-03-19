package com.juandgaines.trackit.domain.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {
    fun timeAndEmits(): Flow<Duration> {
        return flow {
            var lastEmitTime= System.currentTimeMillis()
            while(true){
                delay(200L)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastEmitTime
                emit(elapsedTime.milliseconds)
                lastEmitTime = currentTime
            }
        }
    }

    fun randomFlow(): Flow<Int> {
        return flow {
            while(true){
                delay(1000L)
                emit((0..100).random())
            }
        }
    }

    fun counterFlow(): Flow<Int> {
        return flow {
            var counter = 0
            while(true){
                delay(100)
                emit(counter)
                counter++
            }
        }
    }
}