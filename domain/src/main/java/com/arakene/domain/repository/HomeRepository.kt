package com.arakene.domain.repository

import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult
import java.io.File

interface HomeRepository {

    suspend fun getDailyQuoteNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken>

    suspend fun getDailyQuote(quoteDate: String): ApiResult<DailyQuoteDto>

    suspend fun postLike(likeRequest: LikeRequest, dailyQuoteSeq: Int): ApiResult<SimpleIntResponse>

    suspend fun postUploadImage(imageFile: File, dailyQuoteSeq: Int): ApiResult<SimpleIntResponse>

    suspend fun deleteUploadImage(dailyQuoteSeq: Int): ApiResult<SimpleIntResponse>

}