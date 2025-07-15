package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.util.YN
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
    fun isAlarmPermissionRequestedBefore() : Flow<Boolean>
    suspend fun setAlarmPermissionRequestedBefore(requested: Boolean)

    suspend fun getLocalQuotes(): List<LocalQuoteInfo>
    suspend fun addLocalQuote(quote: LocalQuoteInfo)
    suspend fun deleteQuote(quote: LocalQuoteInfo)
    suspend fun updateQuote(quote: LocalQuoteInfo)
    fun getLocalQuotesPaging(likeYN: YN): Flow<PagingData<LocalQuoteInfo>>
    suspend fun updateLocalQuoteMemo(memo: String, seq: Int)
    suspend fun updateLocalQuoteLike(likeYN: YN, seq: Int): Int
    suspend fun getQuoteLocal(seq: Int): LocalQuoteInfo?

    suspend fun emitTokenExpired(errorCode: String)
    fun getTokenExpired(): Flow<String>

    suspend fun findLocalQuoteById(seq: Int): LocalQuoteInfo?

    suspend fun clear()

    suspend fun deleteQuote(seq: Int)
}