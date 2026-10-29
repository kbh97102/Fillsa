package com.arakene.presentation.util

import com.arakene.domain.responses.MemberQuotesData

sealed interface CalendarEffect : Effect {
    data class ProcessImage(val uri: String, val target: MemberQuotesData) : CalendarEffect
}
