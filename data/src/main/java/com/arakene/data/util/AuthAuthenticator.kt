package com.arakene.data.util

import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.usecase.common.GetAccessTokenUseCase
import com.arakene.domain.usecase.common.GetRefreshTokenUseCase
import com.arakene.domain.usecase.common.SetAccessTokenUseCase
import com.arakene.domain.usecase.common.SetRefreshTokenUseCase
import com.arakene.domain.usecase.common.UpdateTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Dispatcher
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val updateTokenUseCase: UpdateTokenUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val tokens = runBlocking {
            updateTokenUseCase(
                TokenRefreshRequest(
                    deviceId = "",
                    refreshToken = getRefreshTokenUseCase()
                )
            )
        }

        val accessToken = tokens?.accessToken

        // TODO: 나중에 비동기로 처리
        runBlocking {
            setAccessTokenUseCase(accessToken ?: "")
            setRefreshTokenUseCase(tokens?.refreshToken ?: "")
        }

        return if (accessToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}