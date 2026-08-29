package com.arakene.presentation.ui.calendar

import com.arakene.domain.responses.MemberQuotesData
import com.arakene.domain.util.YN
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRecordIndicatorTest {

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
}
