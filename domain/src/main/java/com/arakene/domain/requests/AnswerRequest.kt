package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AnswerRequest(
    @SerializedName("answer")
    val answer: String
)
