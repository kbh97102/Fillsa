package com.arakene.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.MemberQuotesResponse

class GetQuotesListDataSource(
    private val api: FillsaApi,
    private val request: LikeRequest
) : PagingSource<Int, MemberQuotesResponse>() {

    override fun getRefreshKey(state: PagingState<Int, MemberQuotesResponse>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemberQuotesResponse> {
        val page = params.key ?: 0
        return try {
            val response = api.getQuoteList(
                pageable = Pageable(
                    page = page,
                    size = 30
                ), request = request
            )
            val data = response.body()?.content ?: emptyList()

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}