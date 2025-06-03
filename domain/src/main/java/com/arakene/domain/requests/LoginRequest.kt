package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class LoginRequest(
    val loginData: LoginData,
    val syncData: DailySyncData?
)

data class LoginData(

    @SerializedName("deviceData")
    val deviceData: DeviceData,

    @SerializedName("userData")
    val userData: UserData
)

data class DeviceData(
    val deviceId: String,
    val osType: String,
    val appVersion: String,
    val osVersion: String,
    val deviceModel: String
)

data class UserData(
    @SerializedName("oAuthProvider")
    val oAuthProvider: String, // "KAKAO" or "GOOGLE"

    @SerializedName("oAuthId")
    val oAuthId: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)


data class DailySyncData(
    @SerializedName("dailyQuoteSeq")
    val dailyQuoteSeq: Int,

    @SerializedName("typingQuoteRequest")
    val typingQuoteRequest: TypingQuoteRequest,

    @SerializedName("memoRequest")
    val memoRequest: MemoRequest,

    @SerializedName("likeRequest")
    val likeRequest: LikeRequest
)

data class TypingQuoteRequest(
    @SerializedName("typingKorQuote")
    val typingKorQuote: String,

    @SerializedName("typingEngQuote")
    val typingEngQuote: String
)

data class LikeRequest(
    @SerializedName("likeYn")
    val likeYn: String // "Y" or "N"
)