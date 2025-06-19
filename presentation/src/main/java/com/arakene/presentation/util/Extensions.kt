package com.arakene.presentation.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = channelFlow {
    var lastEmissionTime = 0L
    collect {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            send(it)
        }
    }
}