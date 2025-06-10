package com.arakene.data.network

import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.TokenInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST(ApiEndPoint.UPDATE_ACCESS_TOKEN)
    suspend fun updateToken(
        @Body body: TokenRefreshRequest
    ): Response<TokenInfo>

}