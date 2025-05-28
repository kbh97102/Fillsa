package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class LoginRequest(
    val loginData: LoginData,
    val syncData: DailySyncData?
)

data class LoginData(
    @SerializedName("oAuthProvider")
    val oAuthProvider: String, // "KAKAO" or "GOOGLE"

    @SerializedName("tokenData")
    val tokenData: TokenData,

    @SerializedName("userData")
    val userData: UserData
)

data class TokenData(
    @SerializedName("deviceId")
    val deviceId: String,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("expiresIn")
    val expiresIn: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("refreshTokenExpiresIn")
    val refreshTokenExpiresIn: String? = null // 카카오만 해당
)

data class UserData(
    @SerializedName("id")
    val id: String,

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

data class MemoRequest(
    @SerializedName("memo")
    val memo: String
)

data class LikeRequest(
    @SerializedName("likeYn")
    val likeYn: String // "Y" or "N"
)