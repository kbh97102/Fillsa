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

    @SerializedName("completed")
    val completed: Boolean,  // "Y" 또는 "N"

    @SerializedName("likeYn")
    val likeYnString: String,     // "Y" 또는 "N"

    @SerializedName("todayCompleted")
    val todayCompleted: Boolean,

    @SerializedName("engQuote")
    val engQuote: String? = null,

    @SerializedName("engAuthor")
    val engAuthor: String? = null,

    @SerializedName("authorUrl")
    val authorUrl: String? = null,

    @SerializedName("questionKo")
    val questionKo: String? = null,

    @SerializedName("questionEn")
    val questionEn: String? = null,

    @SerializedName("answer")
    val answer: String? = null,

    @SerializedName("answeredAt")
    val answeredAt: String? = null,

    @SerializedName("imagePath")
    val imagePath: String? = null
) {
    val likeYn get() = YN.getYN(likeYnString)

}
