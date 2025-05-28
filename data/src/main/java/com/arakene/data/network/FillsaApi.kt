package com.arakene.data.network

import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FillsaApi {

    @POST(ApiEndPoint.LOGIN)
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

}