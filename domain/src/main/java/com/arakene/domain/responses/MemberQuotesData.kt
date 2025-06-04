package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberQuotesData(
    @SerializedName("dailyQuoteSeq")
    val dailyQuoteSeq: Int,

    @SerializedName("quoteDate")
    val quoteDate: String,

    @SerializedName("quote")
    val quote: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("typingYn")
    val typingYn: String,  // "Y" 또는 "N"

    @SerializedName("likeYn")
    val likeYn: String     // "Y" 또는 "N"
)
