package com.arakene.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arakene.data.network.FillsaApi
import com.arakene.data.network.GetQuotesListDataSource
import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.responses.MemberQuotesResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : ListRepository {

    override fun getQuotesList(
        request: LikeRequest
    ): Flow<PagingData<MemberQuotesResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GetQuotesListDataSource(api, request) }
        ).flow
    }

}