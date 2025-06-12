package com.arakene.domain.requests

import java.time.DayOfWeek

data class LocalQuoteInfo(
    val dailyQuoteSeq: Int,

    val korQuote: String,

    val engQuote: String,

    val korAuthor: String,

    val engAuthor: String,

    val typing: String,

    val likeYn: String,

    val memo: String,

    val date: String,

    val dayOfWeek: String

)