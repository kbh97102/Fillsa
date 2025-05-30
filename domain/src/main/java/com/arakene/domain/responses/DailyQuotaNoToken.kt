package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class DailyQuotaNoToken(
    @SerializedName("dailyQuoteSeq")
    val dailyQuoteSeq: Int,

    @SerializedName("korQuote")
    val korQuote: String?,

    @SerializedName("engQuote")
    val engQuote: String?,

    @SerializedName("korAuthor")
    val korAuthor: String?,

    @SerializedName("engAuthor")
    val engAuthor: String?,

    @SerializedName("authorUrl")
    val authorUrl: String?
)
