package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.MemberQuotesResponse
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    fun getQuotesList(
        request: LikeRequest
    ): Flow<PagingData<MemberQuotesResponse>>

}