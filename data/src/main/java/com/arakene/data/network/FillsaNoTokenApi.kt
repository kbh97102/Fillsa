package com.arakene.data.network

import com.arakene.domain.responses.DailyQuotaNoToken
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FillsaNoTokenApi {

    @GET(ApiEndPoint.GET_DAILY_QUOTE_NON_MEMBER)
    suspend fun getDailyQuoteNonMember(
        @Query("quoteDate") quoteDate: String
    ): Response<DailyQuotaNoToken>

}