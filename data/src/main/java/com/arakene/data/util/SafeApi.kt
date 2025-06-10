package com.arakene.data.util

import android.util.Log
import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.util.ApiResult
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response


suspend fun <T> safeApi(execute: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val result = execute()
        if (result.isSuccessful) {
            ApiResult.Success(data = result.body() ?: return ApiResult.Fail(null))
        } else {

            if (result.code() == 403) {
                return ApiResult.Fail(ErrorResponse.getTokenExpired())
            }

            val parsedError = result.errorBody()?.charStream()?.let {
                Gson().fromJson(it, ErrorResponse::class.java)
            }
            ApiResult.Fail(error = parsedError)
        }
    } catch (e: HttpException) {
        ApiResult.Error(e)
    } catch (e: Exception) {
        ApiResult.Error(e)
    }

}