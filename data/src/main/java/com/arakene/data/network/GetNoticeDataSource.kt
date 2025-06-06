package com.arakene.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arakene.domain.responses.NoticeResponse

class GetNoticeDataSource(
    private val api: FillsaNoTokenApi
) : PagingSource<Int, NoticeResponse>() {

    override fun getRefreshKey(state: PagingState<Int, NoticeResponse>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NoticeResponse> {
        val page = params.key ?: 0
        return try {
            val response = api.getNotice(
                page = page,
                size = 30,
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