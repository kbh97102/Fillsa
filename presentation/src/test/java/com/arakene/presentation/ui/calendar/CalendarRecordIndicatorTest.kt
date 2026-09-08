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
    fun `guest keeps design question while members never substitute missing API question`() {
        val guest = calendarSelectedDayPresentation(null, isMember = false)
        val member = calendarSelectedDayPresentation(null, isMember = true)
        assertEquals("누군가의 호의를 한참 뒤에야 받아들인 적 있나요?", guest.question)
        assertEquals("", member.question)
    }

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
            calendarSelectedDayPresentation(quoteData = null),
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
            ),
        )
    }

    @Test
    fun `production completion presentation never invents an answer or registered image`() {
        val presentation = calendarSelectedDayPresentation(
            MemberQuotesData(
                dailyQuoteSeq = 5,
                quoteDate = "2025-03-21",
                quote = "quote",
                author = "author",
                completed = true,
                likeYnString = YN.Y.type,
                todayCompleted = false,
            ),
        )

        assertTrue(presentation.completed)
        assertFalse(presentation.hasRecordedAnswer)
        assertFalse(presentation.hasRegisteredImage)
        assertEquals(null, presentation.displayedCountOverride)
    }

    @Test
    fun `answered image fixture is deterministic and presentation only`() {
        val fixture = CalendarRuntimeQaFixture(CalendarRuntimeQaState.CompletedAnsweredImage)
        val presentation = fixture.selectedDayPresentation("android.resource://fillsa/fixture")

        assertEquals("2025-03-21", fixture.selectedDay.date.toString())
        assertEquals(4, fixture.data.monthlySummary.typingCount)
        assertEquals(4, fixture.data.monthlySummary.likeCount)
        assertTrue(presentation.hasRecordedAnswer)
        assertTrue(presentation.hasRegisteredImage)
        assertTrue(presentation.answer.contains("\n진심이었다는"))
        assertEquals(0, presentation.displayedCountOverride)
    }

    @Test
    fun `answered image fixture detail first passes through the production monthly mapper`() {
        val fixture = CalendarRuntimeQaFixture(CalendarRuntimeQaState.CompletedAnsweredImage)
        val selected = fixture.data.memberQuotes.single { it.quoteDate == fixture.selectedDay.date.toString() }
        val productionMapped = calendarSelectedDayPresentation(selected, isMember = true)
        val fixturePresentation = fixture.selectedDayPresentation("android.resource://fillsa/fixture")

        assertEquals(productionMapped.question, fixturePresentation.question)
        assertEquals(productionMapped.answer, fixturePresentation.answer)
        assertEquals(productionMapped.answeredAt, fixturePresentation.answeredAt)
        assertEquals(productionMapped.engQuote, fixturePresentation.engQuote)
    }

    @Test
    fun `completed fixtures preserve their binding like action states`() {
        val unanswered = CalendarRuntimeQaFixture(CalendarRuntimeQaState.CompletedUnanswered)
        val answeredImage = CalendarRuntimeQaFixture(CalendarRuntimeQaState.CompletedAnsweredImage)

        assertEquals(
            YN.N,
            unanswered.data.memberQuotes.first { it.quoteDate == unanswered.selectedDay.date.toString() }.likeYn,
        )
        assertEquals(
            YN.Y,
            answeredImage.data.memberQuotes.first { it.quoteDate == answeredImage.selectedDay.date.toString() }.likeYn,
        )
    }

    @Test
    fun `calendar QA state accepts only explicit launch values`() {
        assertEquals(
            CalendarRuntimeQaState.CompletedUnanswered,
            CalendarRuntimeQaState.fromLaunchValue("completed_unanswered"),
        )
        assertEquals(null, CalendarRuntimeQaState.fromLaunchValue("unknown"))
        assertEquals(null, CalendarRuntimeQaState.fromLaunchValue(null))
    }

    @Test
    fun `basic QA grid preserves the binding Figma synthetic cells`() {
        val cells = calendarQaGridCells(CalendarRuntimeQaState.Basic)

        assertEquals(42, cells.size)
        assertEquals(
            listOf("17", "17", "18", "19", "20", "22", "23"),
            cells.subList(21, 28).map { it.label },
        )
        assertTrue(cells[21].selected)
        assertTrue(cells[23].indicators.showFire)
        assertFalse(cells[23].indicators.showHeart)
    }

    @Test
    fun `selected or record-bearing weeks use the shared expanded cell geometry`() {
        assertFalse(calendarWeekUsesExpandedCells(hasSelectedCell = false, hasRecordIndicator = false))
        assertTrue(calendarWeekUsesExpandedCells(hasSelectedCell = true, hasRecordIndicator = false))
        assertTrue(calendarWeekUsesExpandedCells(hasSelectedCell = false, hasRecordIndicator = true))
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

    @Test
    fun `member monthly detail maps server answer image and question fields losslessly`() {
        val quote = MemberQuotesData(
            dailyQuoteSeq = 9,
            quoteDate = "2026-09-08",
            quote = "한국어 명언",
            author = "한국어 작가",
            completed = true,
            likeYnString = YN.Y.type,
            todayCompleted = false,
            engQuote = "English quote",
            engAuthor = "English author",
            authorUrl = "https://example.com/author",
            questionKo = "한국어 질문",
            questionEn = "English question",
            answer = "서버 답변",
            answeredAt = "2026-09-08 12:00:00",
            imagePath = "https://example.com/image.png",
        )

        val presentation = calendarSelectedDayPresentation(quote, isMember = true)

        assertEquals("한국어 질문", presentation.question)
        assertEquals("서버 답변", presentation.answer)
        assertEquals("https://example.com/image.png", presentation.registeredImageUri)
        assertEquals("English quote", presentation.engQuote)
        assertEquals("English author", presentation.engAuthor)
        assertEquals("https://example.com/author", presentation.authorUrl)
        assertTrue(presentation.hasRecordedAnswer)
        assertTrue(presentation.hasRegisteredImage)
    }
}
