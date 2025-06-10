package com.arakene.domain.requests

import androidx.annotation.Keep

@Keep
data class TokenRefreshRequest(
    val deviceId: String,
    val refreshToken: String
)
