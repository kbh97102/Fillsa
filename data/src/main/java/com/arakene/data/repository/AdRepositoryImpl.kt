package com.arakene.data.repository

import android.content.Context
import com.arakene.data.BuildConfig
import com.arakene.data.util.AdCacheManagerImpl
import com.arakene.domain.repository.AdRepository
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AdRepositoryImpl(
    @ApplicationContext private val context: Context,
    private val cacheManagerImpl: AdCacheManagerImpl
) : AdRepository {

    private val id = BuildConfig.ad_native_test

    override suspend fun loadNativeAd(useCache: Boolean): NativeAd? {
        if (useCache) {
            cacheManagerImpl.getCachedAd()?.let { return it }
        }

        return suspendCancellableCoroutine { continuation ->
            val adLoader = AdLoader.Builder(context, id)
                .forNativeAd { ad ->
                    cacheManagerImpl.setCachedAd(ad)
                    continuation.resume(ad)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        continuation.resume(null)
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())

            continuation.invokeOnCancellation {
                // 필요시 cleanup 로직 추가
                cacheManagerImpl.clearCache()
            }
        }
    }
}