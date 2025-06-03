package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemoRequest(
    @SerializedName("memo")
    val memo: String
)