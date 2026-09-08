package com.arakene.presentation.ui.home

import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import com.arakene.presentation.model.HomeMemberQuoteWindow
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HomeWeekStripStateTest {

    private fun memberDay(
        date: String,
        state: String,
        completed: Boolean,
        answer: String?,
    ) = MemberQuoteDay(
        date = date,
        dayOfWeek = "금",
        state = state,
        dailyQuoteSeq = 91,
        korQuote = "오늘의 한국어 명언",
        engQuote = "Today's English quote",
        korAuthor = "한국어 작가",
        engAuthor = "English author",
        authorUrl = "https://example.com/author",
        questionKo = "오늘 무엇을 기록하고 싶나요?",
        questionEn = "What would you like to write today?",
        answer = answer,
        answeredAt = answer?.let { "2026-09-03 14:01:13" },
        likeYn = "N",
        imagePath = null,
        completed = completed,
    )

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
    fun `authenticated week strip preserves server order and completion flags`() {
        val response = MemberWeeklyQuoteResponse(
            startDate = "2026-08-28",
            endDate = "2026-09-03",
            days = listOf(
                memberDay("2026-08-28", "done", completed = false, answer = null),
                memberDay("2026-08-29", "past", completed = false, answer = "답변만 있음"),
                memberDay("2026-08-30", "past", completed = true, answer = null),
                memberDay("2026-08-31", "past", completed = false, answer = null),
                memberDay("2026-09-01", "past", completed = false, answer = null),
                memberDay("2026-09-02", "past", completed = false, answer = null),
                memberDay("2026-09-03", "today", completed = false, answer = null),
            ),
        )

        val days = homeMemberWeekDayStates(HomeMemberQuoteWindow.from(response))

        assertEquals(
            listOf(28, 29, 30, 31, 1, 2, 3),
            days.map { it.date.dayOfMonth },
        )
        assertEquals(
            listOf(true, false, true, false, false, false, false),
            days.map { it.isCompleted },
        )
        assertEquals(LocalDate.of(2026, 9, 3), days.single { it.isSelected }.date)
    }
}
