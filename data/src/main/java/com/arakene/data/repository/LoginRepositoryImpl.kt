package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.LoginRepository
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.LoginResponse
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : LoginRepository {

    override suspend fun login(loginRequest: LoginRequest): ApiResult<LoginResponse> {
        return safeApi {
            api.login(loginRequest)
        }
    }
}