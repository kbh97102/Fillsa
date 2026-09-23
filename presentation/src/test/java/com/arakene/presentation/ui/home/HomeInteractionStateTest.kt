package com.arakene.presentation.ui.home

import com.arakene.presentation.util.HomeAnswerRecordedSnackbar
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.homeStreakCalendarDestination
import com.arakene.presentation.util.isKnownZeroStreak
import com.arakene.presentation.util.recordHomeAnswerForHome
import com.arakene.presentation.util.selectHomeCalendarDate
import com.arakene.presentation.util.toggleHomeCalendar
import com.arakene.presentation.util.Screens
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeInteractionStateTest {

    @Test
    fun `month trigger toggles inline state without producing a navigation destination`() {
        val state = toggleHomeCalendar(isOpen = false, selectedDate = LocalDate.of(2026, 9, 7))

        assertTrue(state.isOpen)
        assertNull(state.refreshDate)
    }

    @Test
    fun `valid selected day closes popup and requests Home refresh`() {
        val selectedDate = LocalDate.of(2026, 9, 6)
        val state = selectHomeCalendarDate(selectedDate)

        assertFalse(state!!.isOpen)
        assertEquals(selectedDate, state.refreshDate)
    }

    @Test
    fun `tooltip is enabled only for a known zero and only its link targets Calendar`() {
        assertFalse(isKnownZeroStreak(null))
        assertFalse(isKnownZeroStreak(1))
        assertTrue(isKnownZeroStreak(0))
        assertEquals(Screens.Calendar, homeStreakCalendarDestination())
    }

    @Test
    fun `only a known zero uses the warning header control`() {
        assertEquals(HomeStreakHeaderPresentation.Streak, homeStreakHeaderPresentation(null))
        assertEquals(HomeStreakHeaderPresentation.Streak, homeStreakHeaderPresentation(3))
        assertEquals(HomeStreakHeaderPresentation.ZeroWarning, homeStreakHeaderPresentation(0))
    }

    @Test
    fun `recording an answer produces the Home snackbar without a Typing destination`() {
        val outcome = recordHomeAnswerForHome(HomeAnswerUiState(draft = "홈에서 기록"))

        assertEquals("홈에서 기록", outcome.state.recordedAnswer)
        assertEquals(HomeAnswerRecordedSnackbar, outcome.snackbarMessage)
    }
}
