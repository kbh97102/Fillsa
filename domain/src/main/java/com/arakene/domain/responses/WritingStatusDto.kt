package com.arakene.domain.responses

import androidx.annotation.Keep

@Keep
data class WritingStatusDto(
    val currentStreak: Int,
    val isTodayWritten: Boolean
)
