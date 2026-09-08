package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AnswerResponse(
    @SerializedName("memberQuoteSeq")
    val memberQuoteSeq: Int,

    @SerializedName("answer")
    val answer: String,

    @SerializedName("answeredAt")
    val answeredAt: String
)
