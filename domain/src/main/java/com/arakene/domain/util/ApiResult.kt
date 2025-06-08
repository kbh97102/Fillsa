package com.arakene.domain.util

import com.arakene.domain.responses.ErrorResponse

sealed interface ApiResult<T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class Fail<T>(val error: ErrorResponse?) : ApiResult<T>

    data class Error<T>(val error: Exception) : ApiResult<T>
}