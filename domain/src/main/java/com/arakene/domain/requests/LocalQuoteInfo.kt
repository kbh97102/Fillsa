package com.arakene.domain.requests

data class LocalQuoteInfo(
    val dailyQuoteSeq: Int,

    val korQuote: String,

    val engQuote: String,

    val korAuthor: String,

    val engAuthor: String,

    val korTyping: String,

    val engTyping: String,

    val likeYn: String,

    val memo: String,

    val date: String,

    val dayOfWeek: String

)