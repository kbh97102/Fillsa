package com.arakene.presentation.ui.home

import com.arakene.presentation.model.HomeMemberFlowState
import com.arakene.presentation.util.HomeRuntimeQaFixture
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    fun `selected streak day keeps its selected circle and shows the existing flame`() {
        val selectedDate = LocalDate.of(2026, 8, 16)
        val selectedDay = homeWeekDayStates(
            selectedDate = selectedDate,
            completedDates = setOf(selectedDate),
        ).last()

        assertEquals(HomeWeekDayPresentation.Selected, homeWeekDayPresentation(selectedDay))
        assertTrue(homeWeekDayShowsBadge(selectedDay))
        assertFalse(homeWeekDayShowsBadge(selectedDay.copy(isCompleted = false)))
    }

    @Test
    fun `scrollable history begins at service start and keeps today last`() {
        val days = homeHistoryDayStates(
            startDate = LocalDate.of(2026, 8, 10),
            endDate = LocalDate.of(2026, 8, 16),
            selectedDate = LocalDate.of(2026, 8, 16),
            completedDates = setOf(LocalDate.of(2026, 8, 11)),
        )
        assertEquals((10..16).toList(), days.map { it.date.dayOfMonth })
        assertEquals(16, days.last().date.dayOfMonth)
        assertEquals(16, days.single { it.isSelected }.date.dayOfMonth)
        assertTrue(days[1].isCompleted)
    }

    @Test
    fun `week strip maps supplied streak dates across month boundary`() {
        val days = homeHistoryDayStates(
            startDate = LocalDate.parse("2026-08-28"),
            endDate = LocalDate.parse("2026-09-03"),
            selectedDate = LocalDate.parse("2026-09-03"),
            completedDates = setOf(LocalDate.parse("2026-08-30"), LocalDate.parse("2026-09-01")),
        )

        assertEquals(
            listOf(28, 29, 30, 31, 1, 2, 3),
            days.map { it.date.dayOfMonth },
        )
        assertEquals(
            listOf(false, false, true, false, true, false, false),
            days.map { it.isCompleted },
        )
        assertEquals(LocalDate.of(2026, 9, 3), days.single { it.isSelected }.date)
    }

    @Test
    fun `week strip does not infer streak from adjacent dates`() {
        val days = homeHistoryDayStates(
            startDate = LocalDate.parse("2026-08-28"),
            endDate = LocalDate.parse("2026-09-03"),
            selectedDate = LocalDate.parse("2026-09-03"),
            completedDates = setOf(LocalDate.parse("2026-08-30"), LocalDate.parse("2026-09-01")),
        )

        assertFalse(days.single { it.date == LocalDate.parse("2026-08-31") }.isCompleted)
        assertEquals(2, days.count { it.isCompleted })
    }

    @Test
    fun `runtime fixture enters Home through the production member day mapper`() {
        val fixture = HomeRuntimeQaFixture(
            quote = "fixture quote",
            author = "fixture author",
            showRecordedAnswer = true,
            showSelectedImageState = true,
        )

        val state = HomeMemberFlowState.from(fixture.memberWindow)
        val selected = state.window.selectedDay!!

        assertEquals("fixture quote", selected.korQuote)
        assertEquals("fixture author", selected.korAuthor)
        assertEquals("기록한 답변", state.answer.recordedAnswer)
        assertEquals("Y", selected.likeYn)
        assertTrue(selected.imagePath?.isNotBlank() == true)
        assertFalse(state.window.days.single { it.date == "2026-08-10" }.completed)
        assertTrue(state.window.days.single { it.date == "2026-08-12" }.completed)
        assertEquals(
            setOf(LocalDate.parse("2026-08-10"), LocalDate.parse("2026-08-11"), LocalDate.parse("2026-08-16")),
            fixture.streakDates,
        )
        val selectedStreakDay = homeHistoryDayStates(
            startDate = LocalDate.parse("2026-08-10"),
            endDate = LocalDate.parse("2026-08-16"),
            selectedDate = LocalDate.parse("2026-08-16"),
            completedDates = fixture.streakDates,
        ).last()
        assertTrue(selectedStreakDay.isCompleted)
        assertEquals(HomeWeekDayPresentation.Selected, homeWeekDayPresentation(selectedStreakDay))
    }

    @Test
    fun `member inline calendar uses the server anchor for today selection and month navigation`() {
        val serverAnchor = LocalDate.parse("2026-08-31")
        val deviceToday = LocalDate.parse("2026-09-01")
        val memberDays = homeMonthGrid(YearMonth.of(2026, 8), serverAnchor, serverAnchor)
        val guestDays = homeMonthGrid(YearMonth.of(2026, 9), deviceToday, deviceToday)

        assertEquals(serverAnchor, memberDays.single { it.isToday }.date)
        assertFalse(isHomeCalendarDateSelectable(deviceToday, serverAnchor))
        assertFalse(isHomeCalendarNextMonthEnabled(YearMonth.of(2026, 8), serverAnchor))
        assertEquals(deviceToday, guestDays.single { it.isToday }.date)
        assertTrue(isHomeCalendarNextMonthEnabled(YearMonth.of(2026, 8), deviceToday))
    }
}
