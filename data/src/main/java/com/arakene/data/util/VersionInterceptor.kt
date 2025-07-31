package com.arakene.data.util

import android.util.Log
import com.arakene.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class VersionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        requestBuilder.addHeader("X-App-Version", BuildConfig.VERSION_NAME)

        Log.e(">>>>", "VERSION ${BuildConfig.VERSION_NAME}")

        return chain.proceed(requestBuilder.build())
    }
}