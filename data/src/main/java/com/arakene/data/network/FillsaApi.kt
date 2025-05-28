package com.arakene.data.network

import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.LoginResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.POST

interface FillsaApi {

    @POST(ApiEndPoint.LOGIN)
    suspend fun login(
        loginRequest: LoginRequest
    ): Response<LoginResponse>

}