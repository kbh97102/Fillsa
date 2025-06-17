package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class LoginRequest(
    val loginData: LoginData,
    val syncData: List<DailySyncData>
)

@Keep
data class LoginData(

    @SerializedName("deviceData")
    val deviceData: DeviceData,

    @SerializedName("userData")
    val userData: UserData
)

@Keep
data class DeviceData(
    val deviceId: String,
    val osType: String,
    val appVersion: String,
    val osVersion: String,
    val deviceModel: String
)

@Keep
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

@Keep
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

@Keep
data class TypingQuoteRequest(
    @SerializedName("typingKorQuote")
    val typingKorQuote: String,

    @SerializedName("typingEngQuote")
    val typingEngQuote: String
)

@Keep
data class LikeRequest(
    @SerializedName("likeYn")
    val likeYn: String // "Y" or "N"
)