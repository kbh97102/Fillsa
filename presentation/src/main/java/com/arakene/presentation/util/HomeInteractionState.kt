package com.arakene.presentation.util

import java.time.LocalDate
import java.time.YearMonth

internal data class HomeCalendarInteractionState(
    val isOpen: Boolean,
    val displayedMonth: YearMonth,
    val refreshDate: LocalDate? = null,
)

internal fun toggleHomeCalendar(isOpen: Boolean, selectedDate: LocalDate): HomeCalendarInteractionState =
    HomeCalendarInteractionState(
        isOpen = !isOpen,
        displayedMonth = YearMonth.from(selectedDate),
    )

internal fun selectHomeCalendarDate(date: LocalDate): HomeCalendarInteractionState? =
    date.takeIf { it in DateCondition.startDay..DateCondition.currentDay() }?.let { selectedDate ->
        HomeCalendarInteractionState(
            isOpen = false,
            displayedMonth = YearMonth.from(selectedDate),
            refreshDate = selectedDate,
        )
    }

internal fun isKnownZeroStreak(streak: Int?): Boolean = streak == 0

internal fun homeStreakCalendarDestination(): Screens = Screens.Calendar

internal const val HomeAnswerRecordedSnackbar = "답변을 기록했어요."

internal data class HomeAnswerRecordOutcome(
    val state: HomeAnswerUiState,
    val snackbarMessage: String,
)

internal fun recordHomeAnswerForHome(state: HomeAnswerUiState): HomeAnswerRecordOutcome =
    HomeAnswerRecordOutcome(
        state = recordHomeAnswer(state),
        snackbarMessage = HomeAnswerRecordedSnackbar,
    )
