package com.arakene.domain.requests

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Pageable(
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int
)
