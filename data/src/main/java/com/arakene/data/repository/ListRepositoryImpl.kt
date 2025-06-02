package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.PageResponseMemberQuotesResponse
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : ListRepository {

    override suspend fun getQuotesList(
        pageable: Pageable,
        request: LikeRequest
    ): ApiResult<PageResponseMemberQuotesResponse> {
        return safeApi {
            api.getQuoteList(pageable, request)
        }
    }
}