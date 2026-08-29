package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.presentation.ui.home.typingInitialAnswerDraft
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeAnswerRouteTest {

    @Test
    fun `answer CTA route preserves the canonical two hundred grapheme draft for typing`() {
        val grapheme = "a\u0301"

        val route = homeAnswerRoute(
            dailyQuote = DailyQuoteDto(quote = "Quote"),
            answer = grapheme.repeat(200) + "x",
        )

        assertEquals(grapheme.repeat(200), route.initialAnswer)
        assertEquals(grapheme.repeat(200), typingInitialAnswerDraft(route.initialAnswer))
    }

    @Test
    fun `empty answer keeps the existing standalone typing route behavior`() {
        val route = homeAnswerRoute(DailyQuoteDto(quote = "Quote"), answer = "")

        assertEquals("", route.initialAnswer)
    }
}
