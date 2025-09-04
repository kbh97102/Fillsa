package com.arakene.domain.util

import com.arakene.domain.responses.ErrorResponse

sealed interface CommonError {
    data class ApiFail(val errorResponse: ErrorResponse) : CommonError
    data class ApiException(val throwable: Throwable) : CommonError
    data object NetworkError : CommonError
    data class TokenExpiredError(val errorResponse: ErrorResponse) : CommonError
    data class VersionMismatchError(val exception: AccessVersionException) : CommonError
    data object DefaultError : CommonError
}