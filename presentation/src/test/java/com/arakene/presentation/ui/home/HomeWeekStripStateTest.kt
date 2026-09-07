package com.arakene.presentation.ui.home

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HomeWeekStripStateTest {

    @Test
    fun `week strip only marks dates supplied by completion data`() {
        val selectedDate = LocalDate.of(2026, 8, 12)

        val days = homeWeekDayStates(
            selectedDate = selectedDate,
            completedDates = emptySet(),
        )

        assertEquals(
            listOf(6, 7, 8, 9, 10, 11, 12),
            days.map { it.date.dayOfMonth },
        )
        assertEquals(selectedDate, days.single { it.isSelected }.date)
        assertFalse(days.first().isCompleted)
        assertEquals(0, days.count { it.isCompleted })
    }

    @Test
    fun `selected weekday appearance wins over a completed marker`() {
        val selectedDate = LocalDate.of(2026, 8, 12)
        val selectedDay = homeWeekDayStates(
            selectedDate = selectedDate,
            completedDates = setOf(selectedDate),
        ).last()

        assertEquals(HomeWeekDayPresentation.Selected, homeWeekDayPresentation(selectedDay))
    }
}
