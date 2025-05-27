package com.arakene.fillsa

import android.app.Application
import com.arakene.presentation.BuildConfig
import com.kakao.sdk.common.KakaoSdk

class FillsaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.kakao_key)

    }

}