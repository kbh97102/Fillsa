package com.arakene.data.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenProvider.getToken() ?: ""

        val original = chain.request()
        val requestBuilder = original.newBuilder()

        if (accessToken.isNotBlank()) {

            Log.d(">>>> Token", "EndPoint ${original.url.encodedPath} query ${original.url.encodedQuery} token $accessToken")

            requestBuilder.addHeader("Authorization", "Bearer 1.0.0")
        }

        return chain.proceed(requestBuilder.build())
    }
}