package com.arakene.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    suspend fun setAccessToken(token: String)
    suspend fun getAccessToken(): String
    suspend fun setRefreshToken(token: String)
    suspend fun getRefreshToken(): String
    suspend fun setImageUri(uri: String)
    fun getImageUri(): Flow<String>
    fun getLoginStatus(): Flow<Boolean>
    suspend fun isFirstOpen(): Flow<Boolean>
    suspend fun setFirstOpen(value: Boolean)
    suspend fun setAlarm(value: Boolean)
    suspend fun setName(value: String)
    fun getAlarm(): Flow<Boolean>
    fun getName(): Flow<String>
}