package com.arakene.presentation.util.state

import java.time.LocalDate

data class QuoteListState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = startDate.plusDays(7),
    val displayCalendar: Boolean = false,
    val likeFilter: Boolean = false,
) : UiState
