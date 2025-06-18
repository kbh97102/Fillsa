package com.arakene.presentation.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

class CursorAnimateState {

    val atomicJob = AtomicReference<Job?>()

    var cursorAlpha by mutableFloatStateOf(0f)
        private set

    suspend fun startBlinking() {
        coroutineScope {

            // 이전에 실행중이던 job 가져오고 다음 신규 이벤트 할당을 위한 null 세팅
            val old = atomicJob.getAndSet(null)


            // 새로운 커서 관리 이벤트 시작
            atomicJob.compareAndSet(
                null,
                launch {
                    old?.cancelAndJoin()

                    try {
                        cursorAlpha = 1f
                        while (true) {
                            delay(500)
                            cursorAlpha = 0f
                            delay(500)
                            cursorAlpha = 1f
                        }
                    } finally {
                        cursorAlpha = 0f
                    }
                }
            )
        }
    }

    fun cancelJob() {
        atomicJob.getAndSet(null)?.cancel()
    }

}