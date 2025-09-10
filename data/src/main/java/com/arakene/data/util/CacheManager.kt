package com.arakene.data.util

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AdCacheManagerImpl(
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private var cachedAd: NativeAd? = null
    private var cacheInvalidationJob: Job? = null

    fun getCachedAd(): NativeAd? = cachedAd

    fun setCachedAd(ad: NativeAd) {
        cachedAd?.destroy()
        cachedAd = ad
        startCacheInvalidationJob()
    }

    fun clearCache() {
        cachedAd?.destroy()
        cachedAd = null
        cacheInvalidationJob?.cancel()
    }

    fun startCacheInvalidationJob() {
        cacheInvalidationJob?.cancel()
        cacheInvalidationJob = coroutineScope.launch {
            delay(3600000L) // 1시간 대기
            clearCache()
        }
    }
}