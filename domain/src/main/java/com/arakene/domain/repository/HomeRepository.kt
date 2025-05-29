package com.arakene.domain.repository

import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.util.ApiResult

interface HomeRepository {

    suspend fun getDailyQuotaNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken>

}