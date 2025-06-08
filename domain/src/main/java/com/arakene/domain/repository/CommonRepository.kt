package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.NoticeResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.responses.TokenInfo
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface CommonRepository {

    fun getNotice(): Flow<PagingData<NoticeResponse>>
    suspend fun deleteResign(): ApiResult<SimpleIntResponse>

    suspend fun updateAccessToken(request: TokenRefreshRequest): TokenInfo?
}