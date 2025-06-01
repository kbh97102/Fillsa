package com.arakene.data.network

import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.LoginResponse
import com.arakene.domain.responses.SimpleIntResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FillsaApi {

    @GET(ApiEndPoint.GET_DAILY_QUOTE)
    suspend fun getDailyQuote(
        @Query("quoteDate") quoteDate: String
    ): Response<DailyQuoteDto>

    @POST(ApiEndPoint.POST_LIKE)
    suspend fun postLike(
        @Path("dailyQuoteSeq") dailyQuoteSeq: Int,
        @Body body: LikeRequest
    ):  Response<SimpleIntResponse>

    @Multipart
    @POST(ApiEndPoint.POST_UPLOAD_IMAGE)
    suspend fun postUploadImage(
        @Path("dailyQuoteSeq") dailyQuoteSeq: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleIntResponse>
}