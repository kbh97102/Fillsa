package com.arakene.domain.util

sealed interface ApiResult<T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class Fail<T>(val error: String) : ApiResult<T>

    data class Error<T>(val error: Exception) : ApiResult<T>
}