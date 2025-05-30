package com.arakene.domain.repository

import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.ApiResult

interface HomeRepository {

    suspend fun getDailyQuoteNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken>

    suspend fun getDailyQuote(quoteDate: String): ApiResult<DailyQuoteDto>

}