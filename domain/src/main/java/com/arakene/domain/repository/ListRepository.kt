package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    fun getQuotesList(
        request: LikeRequest
    ): Flow<PagingData<MemberQuotesResponse>>

    suspend fun postSaveMemo(
        request: MemoRequest,
        memberQuoteSeq: String
    ): ApiResult<SimpleIntResponse>

}