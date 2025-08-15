package com.arakene.domain.repository

import com.arakene.domain.model.AdState
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

interface AdRepository {

    suspend fun loadNativeAd(useCache: Boolean): NativeAd?
}