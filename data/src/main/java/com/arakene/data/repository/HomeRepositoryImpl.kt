package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val nonTokenApi: FillsaNoTokenApi,
    private val api: FillsaApi
): HomeRepository{

    override suspend fun getDailyQuoteNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken> {
        return safeApi {
            nonTokenApi.getDailyQuoteNonMember(quoteDate)
        }
    }

    override suspend fun getDailyQuote(quoteDate: String): ApiResult<DailyQuoteDto> {
        return safeApi {
            api.getDailyQuote(quoteDate)
        }
    }
}