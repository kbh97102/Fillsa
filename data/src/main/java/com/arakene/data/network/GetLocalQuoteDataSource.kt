package com.arakene.data.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.db.LocalQuoteInfoEntity
import com.arakene.domain.util.YN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLocalQuoteDataSource(
    private val dao: LocalQuoteInfoDao,
    private val likeYn: String
) : PagingSource<Int, LocalQuoteInfoEntity>() {

    override fun getRefreshKey(state: PagingState<Int, LocalQuoteInfoEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocalQuoteInfoEntity> {
        val page = params.key ?: 0
        return try {

            val response = withContext(Dispatchers.IO) {
                if (likeYn == YN.Y.type) {
                    dao.getPagingListWithLike(
                        offset = page * 10,
                        likeYn
                    )
                } else {
                    dao.getPagingList(
                        offset = page * 10
                    )
                }
            }

            Log.d(">>>>", "Paging Source page $page ${response}")

            LoadResult.Page(
                data = response,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}