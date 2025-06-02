package com.arakene.domain.requests

import androidx.annotation.Keep

@Keep
data class Pageable(
    val page: Int,
    val size: Int,
    val sort: List<String>
)
