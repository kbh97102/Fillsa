package com.arakene.fillsa

import android.app.Application
import com.arakene.data.util.TokenProvider
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.presentation.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FillsaApplication : Application() {

    @Inject
    lateinit var tokenProvider: TokenProvider

    @Inject
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.kakao_key)

        CoroutineScope(Dispatchers.IO).launch {
            tokenProvider.setToken(getAccessTokenUseCase())
        }

    }

}