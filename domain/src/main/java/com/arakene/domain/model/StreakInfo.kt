package com.arakene.domain.model

import java.time.LocalDate

data class StreakInfo(
    val date: LocalDate, // yyyy-MM-dd

    val streakDateCount: Int = 0,

    val isDailyWritingCompleted: Boolean = false
)
