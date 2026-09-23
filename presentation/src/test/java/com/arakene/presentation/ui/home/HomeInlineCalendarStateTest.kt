package com.arakene.presentation.ui.home

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeInlineCalendarStateTest {

    @Test
    fun `month grid is sunday first and retains visible adjacent dates`() {
        val days = homeMonthGrid(YearMonth.of(2025, 12), LocalDate.of(2025, 12, 25))

        assertEquals(LocalDate.of(2025, 11, 30), days.first().date)
        assertEquals(LocalDate.of(2026, 1, 3), days.last().date)
        assertEquals(35, days.size)
        assertTrue(days.single { it.date == LocalDate.of(2025, 12, 25) }.isSelected)
        assertFalse(days.first().isInDisplayedMonth)
    }

    @Test
    fun `only dates within the existing Home date range are selectable`() {
        assertFalse(isHomeCalendarDateSelectable(LocalDate.of(2025, 6, 9)))
        assertTrue(isHomeCalendarDateSelectable(LocalDate.of(2025, 6, 10)))
    }
}
