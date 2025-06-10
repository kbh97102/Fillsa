package com.arakene.data.util

import android.util.Log
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.usecase.common.GetRefreshTokenUseCase
import com.arakene.domain.usecase.common.SetAccessTokenUseCase
import com.arakene.domain.usecase.common.SetRefreshTokenUseCase
import com.arakene.domain.usecase.common.UpdateTokenUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
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

        Log.e(">>>>TOKEN", "여기오니? ${route?.address} responseCode ${response.code}")

        try {
            val body = response.peekBody(1024 * 1024L).string()

            val errorResponse = Gson().fromJson(body, ErrorResponse::class.java)

            Log.e(">>>>TOKEN", "파싱성공? ${errorResponse}")

            if (response.code == 401 && errorResponse.errorCode != 3003) {
                return null
            }

            Log.e(">>>>TOKEN", "error $errorResponse")
        } catch (e: Exception) {
            e.printStackTrace()
        }



        if (responseCount(response) >= 2) return null

        val tokens = runBlocking {
            updateTokenUseCase(
                TokenRefreshRequest(
                    deviceId = "",
                    refreshToken = getRefreshTokenUseCase()
                )
            ).also { tokenInfo ->
                setAccessTokenUseCase(tokenInfo?.accessToken ?: "")
                setRefreshTokenUseCase(tokenInfo?.refreshToken ?: "")
            }
        }

        val accessToken = tokens?.accessToken

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