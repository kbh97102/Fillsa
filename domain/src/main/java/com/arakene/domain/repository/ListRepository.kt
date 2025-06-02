package com.arakene.domain.repository

import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.PageResponseMemberQuotesResponse
import com.arakene.domain.util.ApiResult

interface ListRepository {

    suspend fun getQuotesList(pageable: Pageable, request: LikeRequest) : ApiResult<PageResponseMemberQuotesResponse>

}