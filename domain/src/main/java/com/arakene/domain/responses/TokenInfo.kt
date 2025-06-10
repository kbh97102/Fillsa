package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class TokenInfo(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)
