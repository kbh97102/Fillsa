package com.arakene.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arakene.data.network.FillsaApi
import com.arakene.data.network.GetQuotesListDataSource
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : ListRepository {

    override suspend fun postSaveMemo(
        request: MemoRequest,
        memberQuoteSeq: String
    ): ApiResult<SimpleIntResponse> {
        return safeApi {
            api.postSaveMemo(body = request, memberQuoteSeq = memberQuoteSeq)
        }
    }

    override fun getQuotesList(
        likeYn: String,
        startDate: String,
        endDate: String
    ): Flow<PagingData<MemberQuotesResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GetQuotesListDataSource(
                    api,
                    likeYn,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        ).flow
    }

}