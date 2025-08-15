package com.arakene.domain.model

import com.google.android.gms.ads.nativead.NativeAd

sealed interface AdState {
    data object Loading : AdState
    data class Success(val nativeAd: NativeAd) : AdState
    data object Error : AdState
}