package com.dj.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {
    fun timeAndEmit(): Flow<Duration> = flow {
        var lastEmitTime = System.nanoTime() // System.currentTimeMillis()
        while(true){
            delay(200L)
            val currentTime = System.nanoTime() // System.currentTimeMillis()
            val elapsedTime = currentTime - lastEmitTime
            emit(elapsedTime.milliseconds)
            lastEmitTime = currentTime
        }
    }
}