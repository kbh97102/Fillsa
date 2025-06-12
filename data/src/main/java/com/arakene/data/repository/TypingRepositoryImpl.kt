package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.TypingRepository
import com.arakene.domain.requests.TypingQuoteRequest
import com.arakene.domain.responses.MemberTypingQuoteResponse
import com.arakene.domain.responses.SimpleIntResponse
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class TypingRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : TypingRepository {

    override suspend fun getTyping(dailyQuoteSeq: Int): ApiResult<MemberTypingQuoteResponse> {
        return safeApi {
            api.getTyping(dailyQuoteSeq)
        }
    }

    override suspend fun postTyping(
        dailyQuoteSeq: Int,
        request: TypingQuoteRequest
    ): ApiResult<SimpleIntResponse> {
        return safeApi {
            api.postTyping(dailyQuoteSeq, request)
        }
    }
}