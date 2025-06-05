package com.arakene.domain.responses

import androidx.annotation.Keep
import com.arakene.domain.util.YN
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
    val typingYnString: String,  // "Y" 또는 "N"

    @SerializedName("likeYn")
    val likeYnString: String     // "Y" 또는 "N"
) {
    val likeYn get() = YN.getYN(likeYnString)
    val typingYn get() = YN.getYN(typingYnString)
}
