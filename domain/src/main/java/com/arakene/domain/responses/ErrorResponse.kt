package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ErrorResponse(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("httpStatus")
    val httpStatus: Int,
    @SerializedName("errorCode")
    val errorCode: Int,
    @SerializedName("message")
    val message: String
){
    companion object{
        fun getTokenExpired() = ErrorResponse(
            timestamp = "",
            httpStatus = 403,
            errorCode = 403,
            message = ""
        )
    }
}