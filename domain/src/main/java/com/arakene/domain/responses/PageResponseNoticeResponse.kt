package com.arakene.domain.responses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PageResponseNoticeResponse(
    @SerializedName("content")
    val content: List<NoticeResponse>,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("currentPage")
    val currentPage: Int
)
