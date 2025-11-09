
package com.arakene.domain.responses

import androidx.annotation.Keep

@Keep
data class MemberStreakResponse(
    val currentStreak: Int,
    val isTodayWritten: Boolean
)
