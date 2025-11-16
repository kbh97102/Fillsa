package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.responses.NoticeResponse
import com.arakene.domain.responses.PopupResponse
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.Flow


interface CommonRepository {

    fun getNotice(): Flow<PagingData<NoticeResponse>>

    suspend fun getMemberStreaks(): ApiResult<MemberStreakResponse>

    suspend fun getPopUpGeneral(): ApiResult<PopupResponse>

    suspend fun deleteResign(): ApiResult<Int>

}