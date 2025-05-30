package com.arakene.domain.repository

import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.LoginResponse
import com.arakene.domain.util.ApiResult

interface LoginRepository {

    suspend fun login(loginRequest: LoginRequest): ApiResult<LoginResponse>

}