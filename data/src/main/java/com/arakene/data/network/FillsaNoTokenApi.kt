package com.arakene.data.network

import com.arakene.domain.responses.DailyQuotaNoToken
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FillsaNoTokenApi {

    @GET(ApiEndPoint.GET_WISE_SAYING_NON_MEMBER)
    suspend fun getWiseSayingNonMember(
        @Query("quoteDate") quoteDate: String
    ): Response<DailyQuotaNoToken>

}