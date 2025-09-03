package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberQuoteImageResponse(
    val memberQuoteSeq: Int,
    @SerializedName("imagePath")
    val imagePath: String
)
