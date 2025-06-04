package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MonthlySummaryData(
    @SerializedName("typingCount")
    val typingCount: Int,

    @SerializedName("likeCount")
    val likeCount: Int
)