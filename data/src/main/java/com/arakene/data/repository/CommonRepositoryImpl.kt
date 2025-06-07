package com.arakene.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.network.GetNoticeDataSource
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.responses.NoticeResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommonRepositoryImpl @Inject constructor(
    private val noTokenApi: FillsaNoTokenApi,
    private val api: FillsaApi
) : CommonRepository {

    override fun getNotice(): Flow<PagingData<NoticeResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GetNoticeDataSource(noTokenApi) }
        ).flow
    }


    override suspend fun deleteResign(): ApiResult<SimpleIntResponse> {
        return safeApi {
            api.deleteResign()
        }
    }
}