package com.arakene.data.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.util.AccessVersionException

class GetQuotesListDataSource(
    private val api: FillsaApi,
    private val likeYn: String
) : PagingSource<Int, MemberQuotesResponse>() {

    override fun getRefreshKey(state: PagingState<Int, MemberQuotesResponse>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemberQuotesResponse> {
        val page = params.key ?: 0
        Log.e(">>>>", "여기안오니? 11")
        return try {
            val response = api.getQuoteList(
                page = page,
                size = 30,
                likeYn = likeYn
            )

            if (response.isSuccessful) {
                val data = response.body()?.content.orEmpty()
                val totalPage = response.body()?.totalPages ?: 0
                val currentPage = response.body()?.currentPage ?: 0

                val isLast = data.isEmpty() || totalPage - currentPage == 1

                Log.e(">>>>", "response Code ${response.code()}")

                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (isLast) null else page + 1
                )
            } else {
                LoadResult.Error(AccessVersionException())
            }
        } catch (e: Exception) {
            Log.e(">>>>", "여기안오니? 22 $e")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}