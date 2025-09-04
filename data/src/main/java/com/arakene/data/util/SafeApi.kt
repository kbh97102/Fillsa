package com.arakene.data.util

import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.util.ApiResult
import com.arakene.domain.util.CommonError
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException


suspend fun <T> safeApi(execute: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val result = execute()
        if (result.isSuccessful) {
            ApiResult.Success(
                data = result.body() ?: return ApiResult.Fail(
                    CommonError.ApiFail(ErrorResponse.defaultError())
                )
            )
        } else {

            when (result.code()) {
                401, 403 -> {
                    return ApiResult.Fail(CommonError.TokenExpiredError(errorResponse = ErrorResponse.getTokenExpired()))
                }

                else -> {
                    val parsedError = result.errorBody()?.charStream()?.let {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    }
                    return ApiResult.Fail(
                        CommonError.ApiFail(parsedError ?: ErrorResponse.defaultError())
                    )
                }
            }
        }
    } catch (e: HttpException) {
        ApiResult.Fail(CommonError.NetworkError)
    } catch (e: UnknownHostException) {
        ApiResult.Fail(CommonError.NetworkError)
    } catch (e: Exception) {
        ApiResult.Fail(CommonError.ApiException(e))
    }

}