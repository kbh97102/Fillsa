package com.arakene.domain.responses

import androidx.annotation.Keep

@Keep
data class MemberQuoteImageResponse(
    val memberQuoteSeq: Int,
    val imagePath: String
)
