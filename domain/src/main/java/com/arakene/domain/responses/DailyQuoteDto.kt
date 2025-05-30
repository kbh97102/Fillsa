package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DailyQuoteDto(
    @SerializedName("likeYn")
    val likeYn: String,

    @SerializedName("imagePath")
    val imagePath: String?,

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
){

    constructor() : this(
        likeYn = "",
        imagePath = "",
        dailyQuoteSeq = 0,
        korQuote = "",
        engQuote = "",
        korAuthor = "",
        engAuthor = "",
        authorUrl = ""
    )

    constructor(quote: String) : this(
        likeYn = "",
        imagePath = "",
        dailyQuoteSeq = 0,
        korQuote = quote,
        engQuote = "",
        korAuthor = "",
        engAuthor = "",
        authorUrl = ""
    )
}