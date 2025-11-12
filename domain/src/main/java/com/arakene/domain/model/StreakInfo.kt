package com.arakene.domain.model

data class StreakInfo(
    val date: String, // yyyy-MM-dd

    val streakDateCount: Int = 0,

    val isDailyWritingCompleted: Boolean = false
)
