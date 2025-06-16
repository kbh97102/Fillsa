package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.CalendarRepository
import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MonthlyQuoteResponse
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val api: FillsaApi,
    private val noTokenApi: FillsaNoTokenApi
) : CalendarRepository {

    override suspend fun getQuotesMonthlyNonMember(yearMonth: String): ApiResult<MonthlyQuoteResponse> {
        return safeApi {
            noTokenApi.getMonthlyQuotesNonMember(yearMonth)
        }
    }

    override suspend fun getQuotesMonthly(yearMonth: String): ApiResult<MemberMonthlyQuoteResponse> {
        return safeApi {
            api.getQuotesMonthly(yearMonth)
        }
    }
}