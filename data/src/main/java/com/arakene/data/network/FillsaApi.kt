package com.arakene.data.network

import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.MemoRequest
import com.arakene.domain.requests.Pageable
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.PageResponseMemberQuotesResponse
import com.arakene.domain.responses.SimpleIntResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    ): Response<SimpleIntResponse>

    @Multipart
    @POST(ApiEndPoint.POST_UPLOAD_IMAGE)
    suspend fun postUploadImage(
        @Path("dailyQuoteSeq") dailyQuoteSeq: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleIntResponse>


    @DELETE(ApiEndPoint.POST_UPLOAD_IMAGE)
    suspend fun deleteUploadImage(
        @Path("dailyQuoteSeq") dailyQuoteSeq: Int,
    ): Response<SimpleIntResponse>

    @GET(ApiEndPoint.GET_QUOTE_LIST)
    suspend fun getQuoteList(
        @Query("pageable") pageable: Pageable,
        @Query("request") request: LikeRequest
    ): Response<PageResponseMemberQuotesResponse>

    @POST(ApiEndPoint.POST_SAVE_MEMO)
    suspend fun postSaveMemo(
        @Path("memberQuoteSeq") memberQuoteSeq: String,
        @Body body: MemoRequest
    ): Response<SimpleIntResponse>

}