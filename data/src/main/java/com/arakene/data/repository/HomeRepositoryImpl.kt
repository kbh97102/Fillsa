package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.requests.AnswerRequest
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.responses.AnswerResponse
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberQuoteImageResponse
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.domain.util.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val nonTokenApi: FillsaNoTokenApi,
    private val api: FillsaApi
) : HomeRepository {

    override suspend fun testErrorCode(code: Int): ApiResult<Unit> {
        return safeApi {
            nonTokenApi.testErrorCode(code)
        }
    }

    override suspend fun postLike(
        likeRequest: LikeRequest,
        dailyQuoteSeq: Int
    ): ApiResult<Int> {
        return safeApi {
            api.postLike(dailyQuoteSeq, likeRequest)
        }
    }

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

    override suspend fun getWeeklyQuotes(endDate: String?): ApiResult<MemberWeeklyQuoteResponse> {
        return safeApi {
            api.getWeeklyQuotes(endDate)
        }
    }

    override suspend fun getMemberQuoteDay(quoteDate: String): ApiResult<MemberQuoteDay> {
        return safeApi {
            api.getMemberQuoteDay(quoteDate)
        }
    }

    override suspend fun postAnswer(
        dailyQuoteSeq: Int,
        request: AnswerRequest
    ): ApiResult<AnswerResponse> {
        return safeApi {
            api.postAnswer(dailyQuoteSeq, request)
        }
    }

    override suspend fun postUploadImage(
        imageFile: File,
        dailyQuoteSeq: Int
    ): ApiResult<MemberQuoteImageResponse> {
        return safeApi {
            api.postUploadImage(
                dailyQuoteSeq = dailyQuoteSeq,
                MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
            )
        }
    }

    override suspend fun deleteUploadImage(dailyQuoteSeq: Int): ApiResult<Int> {
        return safeApi {
            api.deleteUploadImage(dailyQuoteSeq)
        }
    }
}
