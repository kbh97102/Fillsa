package com.arakene.domain.util

sealed interface ApiResult<T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class Fail<T>(val error: CommonError) : ApiResult<T>
}