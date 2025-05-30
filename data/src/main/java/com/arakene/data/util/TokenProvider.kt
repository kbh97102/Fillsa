package com.arakene.data.util

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 토큰 인터셉터에서 토큰 가져오는 유즈케이스를 쓰려면 runBlocking을 해야하는데 속도가 매우매우 느림
 * 약 6초정도가 걸렸음 따라서 액세스 토큰을 캐싱하는 방법을 사용
 */
@Singleton
class TokenProvider @Inject constructor() {
    @Volatile
    private var accessToken: String? = null

    fun getToken(): String? = accessToken

    fun setToken(token: String?) {
        accessToken = token
    }

    fun clearToken() {
        accessToken = null
    }
}