package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.CalendarRepository
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val api: FillsaApi
) : CalendarRepository {
    override suspend fun getQuotesMonthly(yearMonth: String): ApiResult<MemberMonthlyQuoteResponse> {
        return safeApi {
            api.getQuotesMonthly(yearMonth)
        }
    }
}