package com.arakene.data.network

import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.LoginResponse
import com.arakene.domain.responses.MonthlyQuoteResponse
import com.arakene.domain.responses.PageResponseNoticeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FillsaNoTokenApi {

    @GET(ApiEndPoint.GET_DAILY_QUOTE_NON_MEMBER)
    suspend fun getDailyQuoteNonMember(
        @Query("quoteDate") quoteDate: String
    ): Response<DailyQuotaNoToken>

    @POST(ApiEndPoint.LOGIN)
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET(ApiEndPoint.GET_NOTICE)
    suspend fun getNotice(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): Response<PageResponseNoticeResponse>

    @GET(ApiEndPoint.GET_MONTHLY_QUOTES)
    suspend fun getMonthlyQuotesNonMember(
        @Query("yearMonth") yearMonth: String
    ): Response<List<MonthlyQuoteResponse>>

}