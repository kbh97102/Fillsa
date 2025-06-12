package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberTypingQuoteResponse(
    @SerializedName("korQuote")
    val korQuote: String?,

    @SerializedName("engQuote")
    val engQuote: String?,

    @SerializedName("typingKorQuote")
    val typingKorQuote: String?,

    @SerializedName("typingEngQuote")
    val typingEngQuote: String?,

    @SerializedName("likeYn")
    val likeYn: String
)
