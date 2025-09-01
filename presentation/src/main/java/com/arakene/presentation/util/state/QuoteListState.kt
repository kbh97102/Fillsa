package com.arakene.presentation.util.state

import java.time.LocalDate

data class QuoteListState(
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val displayCalendar: Boolean = false,
    val likeFilter: Boolean = false,
) : UiState
