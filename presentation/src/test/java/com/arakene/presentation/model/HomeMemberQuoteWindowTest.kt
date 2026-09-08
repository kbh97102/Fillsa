package com.arakene.presentation.model

import com.arakene.domain.responses.MemberQuoteDay
import com.arakene.domain.responses.MemberWeeklyQuoteResponse
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMemberQuoteWindowTest {

    private fun memberDay(
        date: String,
        state: String,
        dailyQuoteSeq: Int?,
        answer: String?,
        answeredAt: String?,
        completed: Boolean,
    ) = MemberQuoteDay(
        date = date,
        dayOfWeek = "금",
        state = state,
        dailyQuoteSeq = dailyQuoteSeq,
        korQuote = "오늘의 한국어 명언",
        engQuote = "Today's English quote",
        korAuthor = "한국어 작가",
        engAuthor = "English author",
        authorUrl = "https://example.com/author",
        questionKo = "오늘 무엇을 기록하고 싶나요?",
        questionEn = "What would you like to write today?",
        answer = answer,
        answeredAt = answeredAt,
        likeYn = "N",
        imagePath = null,
        completed = completed,
    )

    private val weeklyFixture = MemberWeeklyQuoteResponse(
        startDate = "2026-08-28",
        endDate = "2026-09-03",
        days = listOf(
            memberDay("2026-08-28", "done", 81, "기록 1", "2026-08-28 09:00:00", true),
            memberDay("2026-08-29", "past", 82, null, null, false),
            memberDay("2026-08-30", "past", 83, null, null, false),
            memberDay("2026-08-31", "past", 84, null, null, false),
            memberDay("2026-09-01", "past", 85, null, null, false),
            memberDay("2026-09-02", "past", 86, null, null, false),
            memberDay("2026-09-03", "today", 87, null, null, false),
        ),
    )

    private val previousWeeklyFixture = MemberWeeklyQuoteResponse(
        startDate = "2026-08-21",
        endDate = "2026-08-27",
        days = listOf(
            memberDay("2026-08-21", "done", 74, "기록 0", "2026-08-21 09:00:00", true),
            memberDay("2026-08-22", "past", 75, null, null, false),
            memberDay("2026-08-23", "past", 76, null, null, false),
            memberDay("2026-08-24", "past", 77, null, null, false),
            memberDay("2026-08-25", "past", 78, null, null, false),
            memberDay("2026-08-26", "past", 79, null, null, false),
            memberDay("2026-08-27", "today", 80, null, null, false),
        ),
    )

    private val unfinishedDay = weeklyFixture.days.last()

    @Test
    fun `initial window selects the server today and preserves server order`() {
        val window = HomeMemberQuoteWindow.from(weeklyFixture)

        assertEquals(LocalDate.of(2026, 9, 3), window.selectedDate)
        assertEquals(
            listOf(
                LocalDate.of(2026, 8, 28),
                LocalDate.of(2026, 8, 29),
                LocalDate.of(2026, 8, 30),
                LocalDate.of(2026, 8, 31),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 3),
            ),
            window.visibleDates,
        )
    }

    @Test
    fun `initial window falls back to the response end date when today is absent`() {
        val responseWithoutToday = weeklyFixture.copy(
            days = weeklyFixture.days.map { day -> day.copy(state = "past") },
        )

        val window = HomeMemberQuoteWindow.from(responseWithoutToday)

        assertEquals(LocalDate.of(2026, 9, 3), window.selectedDate)
    }

    @Test
    fun `window boundaries derive from response endDate`() {
        val window = HomeMemberQuoteWindow.from(weeklyFixture)

        assertEquals("2026-08-27", window.requestEndDate(WindowDirection.Previous))
        assertEquals("2026-09-10", window.requestEndDate(WindowDirection.Next))
    }

    @Test
    fun `answer patch never completes a quote`() {
        val updated = unfinishedDay.withAnswer("기록", "2026-09-03 14:01:13")

        assertFalse(updated.completed)
        assertEquals("today", updated.state)
        assertEquals("기록", updated.answer)
        assertEquals("2026-09-03 14:01:13", updated.answeredAt)
    }

    @Test
    fun `daily quote mapping uses zero only when the server sequence is absent`() {
        val mapped = unfinishedDay.copy(dailyQuoteSeq = null).toDailyQuoteDto()
        val mappedWithServerSequence = unfinishedDay.toDailyQuoteDto()

        assertEquals(0, mapped.dailyQuoteSeq)
        assertEquals(87, mappedWithServerSequence.dailyQuoteSeq)
        assertEquals("N", mapped.likeYn)
        assertNull(mapped.imagePath)
        assertEquals("오늘의 한국어 명언", mapped.korQuote)
        assertEquals("Today's English quote", mapped.engQuote)
        assertEquals("한국어 작가", mapped.korAuthor)
        assertEquals("English author", mapped.engAuthor)
        assertEquals("https://example.com/author", mapped.authorUrl)
        assertEquals("2026-09-03", mapped.quoteDate)
    }

    @Test
    fun `selecting a date outside the window returns no replacement`() {
        val selected = HomeMemberQuoteWindow.from(weeklyFixture)
            .select(LocalDate.of(2026, 9, 4))

        assertNull(selected)
    }

    @Test
    fun `session cache keys each complete window by its end date`() {
        val currentWindow = HomeMemberQuoteWindow.from(weeklyFixture)
        val previousWindow = HomeMemberQuoteWindow.from(previousWeeklyFixture)

        val cached = emptyMap<LocalDate, HomeMemberQuoteWindow>()
            .withCachedWindow(currentWindow)
            .withCachedWindow(previousWindow)

        assertEquals(2, cached.size)
        assertSame(currentWindow, cached[LocalDate.of(2026, 9, 3)])
        assertSame(previousWindow, cached[LocalDate.of(2026, 8, 27)])
    }

    @Test
    fun `response acceptance rejects a request older than the latest request`() {
        assertTrue(shouldAcceptHomeMemberQuoteResponse(requestId = 42, latestRequestId = 42))
        assertFalse(shouldAcceptHomeMemberQuoteResponse(requestId = 41, latestRequestId = 42))
    }
}
