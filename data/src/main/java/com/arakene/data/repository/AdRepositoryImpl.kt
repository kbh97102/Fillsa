package com.arakene.data.repository

import android.content.Context
import com.arakene.data.BuildConfig
import com.arakene.domain.model.AdState
import com.arakene.domain.repository.AdRepository
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class AdRepositoryImpl(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : AdRepository {

    private val _currentNativeAd = MutableStateFlow<NativeAd?>(null)

    private val _adState = MutableStateFlow<AdState>(AdState.Loading)

    private var isLoadingAdInternal = false

    private var cacheInvalidationJob: Job? = null

    private val adUnitId = BuildConfig.ad_native_test

    override fun getAdStateFlow(): StateFlow<AdState> {
        if (adUnitId.isNotBlank() && _currentNativeAd.value == null) {
            loadAdInternal()
        }

        return _adState
    }

    private fun loadAdInternal() {
        if (isLoadingAdInternal) {
            return
        }

        isLoadingAdInternal = true
        _adState.value = AdState.Loading

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                _currentNativeAd.value?.destroy()
                _currentNativeAd.value = ad
                isLoadingAdInternal = false
                _adState.value = AdState.Success(ad)
                startCacheInvalidationJob()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAdInternal = false
                    _currentNativeAd.value?.destroy()
                    _currentNativeAd.value = null
                    _adState.value = AdState.Error
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * 1시간 후 캐시를 지우고 새로운 광고를 자동으로 로드하는 타이머를 시작합니다.
     */
    private fun startCacheInvalidationJob() {
        cacheInvalidationJob?.cancel()
        cacheInvalidationJob = coroutineScope.launch {
            delay(3600000L)

            _currentNativeAd.value?.destroy()
            _currentNativeAd.value = null
            _adState.value = AdState.Loading

            // 캐시 만료 시 이전에 요청했던 adUnitId를 사용하여 새로운 광고 자동 로드
            loadAdInternal()
        }
    }

    fun clearCache() {
        cacheInvalidationJob?.cancel() // 타이머 취소
        _currentNativeAd.value?.destroy() // 광고 객체 소멸
        _currentNativeAd.value = null // 캐시 초기화
        _adState.value = AdState.Error // 상태를 오류로 변경 (더 이상 유효한 광고 없음)
        isLoadingAdInternal = false // 로딩 상태 초기화
    }
}