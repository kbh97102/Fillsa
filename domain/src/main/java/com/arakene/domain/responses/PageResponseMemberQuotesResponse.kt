package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PageResponseMemberQuotesResponse(
    @SerializedName("content")
    val content: List<MemberQuotesResponse>,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("currentPage")
    val currentPage: Int
)

@Keep
data class MemberQuotesResponse(
    @SerializedName("memberQuoteSeq")
    val memberQuoteSeq: Int,

    @SerializedName("quoteDate")
    val quoteDate: String,

    @SerializedName("quoteDayOfWeek")
    val quoteDayOfWeek: String,

    @SerializedName("quote")
    val quote: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("authorUrl")
    val authorUrl: String,

    @SerializedName("memo")
    val memo: String?,

    @SerializedName("memoYn")
    val memoYn: String, // "Y" or "N"

    @SerializedName("likeYn")
    val likeYn: String  // "Y" or "N"
){
    constructor() : this(
        memberQuoteSeq = 0,
        quoteDate = "",
        quoteDayOfWeek = "",
        quote = "",
        author = "",
        authorUrl = "",
        memo = "",
        memoYn = "N",
        likeYn = "N"
    )
}