package com.arakene.data.util

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
            val parsedError = result.errorBody()?.charStream()?.let {
                Gson().fromJson(it, ErrorResponse::class.java)
            }
            ApiResult.Fail(error = parsedError)
        }
    } catch (e: HttpException) {
        // TODO: 차후에 인터넷 오류 팝업 노출
        ApiResult.Error(e)
    } catch (e: Exception) {
        ApiResult.Error(e)
    }

}