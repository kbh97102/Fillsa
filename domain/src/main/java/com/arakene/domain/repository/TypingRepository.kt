package com.arakene.domain.repository

import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.responses.MemberTypingQuoteResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult

interface TypingRepository {

    suspend fun getTyping(dailyQuoteSeq: Int): ApiResult<MemberTypingQuoteResponse>
    suspend fun postTyping(
        dailyQuoteSeq: Int,
        request: TypingQuoteRequest
    ): ApiResult<SimpleIntResponse>
}