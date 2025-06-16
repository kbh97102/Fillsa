package com.arakene.domain.repository

import com.arakene.domain.responses.MemberMonthlyQuoteResponse
import com.arakene.domain.responses.MemberQuotesResponse
import com.arakene.domain.responses.MonthlyQuoteResponse
import com.arakene.domain.util.ApiResult

interface CalendarRepository {

    suspend fun getQuotesMonthly(
        yearMonth: String
    ): ApiResult<MemberMonthlyQuoteResponse>

    suspend fun getQuotesMonthlyNonMember(
        yearMonth: String
    ): ApiResult<MonthlyQuoteResponse>

}