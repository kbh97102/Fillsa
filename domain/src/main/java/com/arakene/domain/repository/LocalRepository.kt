package com.arakene.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    suspend fun setAccessToken(token: String)
    suspend fun getAccessToken(): String
    suspend fun setRefreshToken(token: String)
    suspend fun getRefreshToken(): String
    fun getLoginStatus(): Flow<Boolean>
}