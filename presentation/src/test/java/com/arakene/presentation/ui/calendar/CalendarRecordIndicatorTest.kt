package com.arakene.presentation.ui.calendar

import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.util.YN
import com.arakene.presentation.util.toKoreanShort
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRecordIndicatorTest {

    @Test
    fun `weekday labels are ordered Sunday through Saturday for the Sunday-first Figma grid`() {
        assertEquals(
            listOf("일", "월", "화", "수", "목", "금", "토"),
            calendarWeekdayOrder().map { it.toKoreanShort() },
        )
    }

    @Test
    fun `empty calendar presentation always includes the fully sized quote card`() {
        val content = calendarDetailContent(CalendarSelectedDayPresentation.Empty)

        assertTrue(content.showEmptyMessage)
        assertTrue(content.showQuoteCard)
        assertEquals(80, content.quoteCardHeightDp)
    }

    @Test
    fun `selected day presentation follows completion data instead of quote text`() {
        assertEquals(
            CalendarSelectedDayPresentation.Empty,
            calendarSelectedDayPresentation(quoteData = null, expanded = false),
        )
        assertEquals(
            CalendarSelectedDayPresentation.Empty,
            calendarSelectedDayPresentation(
                quoteData = MemberQuotesData(
                    dailyQuoteSeq = 3,
                    quoteDate = "2025-03-19",
                    quote = "A quote must not create a completion state.",
                    author = "author",
                    completed = false,
                    likeYnString = YN.N.type,
                    todayCompleted = false,
                ),
                expanded = false,
            ),
        )
        assertEquals(
            CalendarSelectedDayPresentation.Completed,
            calendarSelectedDayPresentation(
                quoteData = MemberQuotesData(
                    dailyQuoteSeq = 4,
                    quoteDate = "2025-03-20",
                    quote = "quote",
                    author = "author",
                    completed = false,
                    likeYnString = YN.N.type,
                    todayCompleted = true,
                ),
                expanded = false,
            ),
        )
        assertEquals(
            CalendarSelectedDayPresentation.Expanded,
            calendarSelectedDayPresentation(
                quoteData = MemberQuotesData(
                    dailyQuoteSeq = 5,
                    quoteDate = "2025-03-21",
                    quote = "quote",
                    author = "author",
                    completed = true,
                    likeYnString = YN.N.type,
                    todayCompleted = false,
                ),
                expanded = true,
            ),
        )
    }

    @Test
    fun `daily writing and like data maps to the Figma fire and heart indicators`() {
        val indicators = calendarRecordIndicators(
            MemberQuotesData(
                dailyQuoteSeq = 1,
                quoteDate = "2025-03-17",
                quote = "quote",
                author = "author",
                completed = true,
                likeYnString = YN.Y.type,
                todayCompleted = false,
            ),
        )

        assertTrue(indicators.showFire)
        assertTrue(indicators.showHeart)
    }

    @Test
    fun `missing daily data leaves both Figma record indicators hidden`() {
        val indicators = calendarRecordIndicators(null)

        assertFalse(indicators.showFire)
        assertFalse(indicators.showHeart)
    }

    @Test
    fun `today completion renders the Figma fire indicator even without a completed record`() {
        val indicators = calendarRecordIndicators(
            MemberQuotesData(
                dailyQuoteSeq = 2,
                quoteDate = "2025-03-18",
                quote = "quote",
                author = "author",
                completed = false,
                likeYnString = YN.N.type,
                todayCompleted = true,
            ),
        )

        assertTrue(indicators.showFire)
        assertFalse(indicators.showHeart)
    }
}
