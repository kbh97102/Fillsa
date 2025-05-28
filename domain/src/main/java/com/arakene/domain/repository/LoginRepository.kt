package com.arakene.domain.repository

import com.arakene.domain.requests.LoginRequest

interface LoginRepository {

    suspend fun login(loginRequest: LoginRequest)

}