package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NoticeResponse(
    @SerializedName("noticeSeq")
    val noticeSeq: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("createdAt")
    val createdAt: String
)
