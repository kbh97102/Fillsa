package com.arakene.data.network

import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FillsaApi {

    @GET(ApiEndPoint.GET_DAILY_QUOTE)
    suspend fun getDailyQuote(
        @Query("quoteDate") quoteDate: String
    ): Response<DailyQuoteDto>

}