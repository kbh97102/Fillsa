package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MonthlyQuoteResponse(
    @SerializedName("dailyQuoteSeq")
    val dailyQuoteSeq: Int,
    @SerializedName("quoteDate")
    val quoteDate: String,
    @SerializedName("quote")
    val quote: String,
    @SerializedName("author")
    val author: String
)
