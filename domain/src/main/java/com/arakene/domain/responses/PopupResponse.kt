package com.arakene.domain.responses

import androidx.annotation.Keep

@Keep
data class PopupResponse(
    val popupSeq: Int,
    val popupType: String,
    val title: String?,
    val content: String?,
    val imageUrl: String?
)
