package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TypingSaveResponse(
    @SerializedName("memberQuoteSeq") val memberQuoteSeq: Int,
    @SerializedName("completedChanged") val completedChanged: Boolean,
    @SerializedName("todayCompletedChanged") val todayCompletedChanged: Boolean,
)
