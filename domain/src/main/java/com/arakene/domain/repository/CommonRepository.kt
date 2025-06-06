package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.responses.NoticeResponse
import kotlinx.coroutines.flow.Flow

interface CommonRepository {

    fun getNotice(): Flow<PagingData<NoticeResponse>>

}