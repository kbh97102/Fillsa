package com.arakene.domain.repository

import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.AnswerRequest
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberQuoteImageResponse
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.domain.util.ApiResult
import java.io.File

interface HomeRepository {

    suspend fun testErrorCode(code: Int): ApiResult<Unit>

    suspend fun getDailyQuoteNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken>

    suspend fun getDailyQuote(quoteDate: String): ApiResult<DailyQuoteDto>

    suspend fun getWeeklyQuotes(endDate: String? = null): ApiResult<MemberWeeklyQuoteResponse>

    suspend fun getMemberQuoteDay(quoteDate: String): ApiResult<MemberQuoteDay>

    suspend fun postAnswer(
        dailyQuoteSeq: Int,
        request: AnswerRequest
    ): ApiResult<AnswerResponse>

    suspend fun postLike(likeRequest: LikeRequest, dailyQuoteSeq: Int): ApiResult<Int>

    suspend fun postUploadImage(imageFile: File, dailyQuoteSeq: Int): ApiResult<MemberQuoteImageResponse>

    suspend fun deleteUploadImage(dailyQuoteSeq: Int): ApiResult<Int>

}
