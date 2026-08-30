package com.arakene.presentation.util

import com.arakene.domain.responses.DailyQuoteDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeTypingNavigationTest {

    @Test
    fun `loading quote cannot navigate to typing even when a previous quote exists`() {
        assertNull(
            homeTypingDestination(
                quote = loadedQuote(),
                localeType = LocaleType.KOR,
                loadState = HomeQuoteLoadState.Loading,
            ),
        )
    }

    @Test
    fun `empty default quote cannot navigate to typing`() {
        assertNull(
            homeTypingDestination(
                quote = DailyQuoteDto(),
                localeType = LocaleType.KOR,
                loadState = HomeQuoteLoadState.Loaded,
            ),
        )
    }

    @Test
    fun `loaded quote missing the selected locale cannot navigate to typing`() {
        assertNull(
            homeTypingDestination(
                quote = loadedQuote(engQuote = ""),
                localeType = LocaleType.ENG,
                loadState = HomeQuoteLoadState.Loaded,
            ),
        )
    }

    @Test
    fun `loaded localized quote navigates to typing with its original sequence`() {
        val destination = homeTypingDestination(
            quote = loadedQuote(),
            localeType = LocaleType.KOR,
            loadState = HomeQuoteLoadState.Loaded,
        )

        assertEquals(73, destination?.dailyQuoteDto?.dailyQuoteSeq)
        assertEquals("오늘의 글", destination?.dailyQuoteDto?.korQuote)
    }

    private fun loadedQuote(engQuote: String = "Today's writing") = DailyQuoteDto(
        likeYn = "N",
        imagePath = null,
        dailyQuoteSeq = 73,
        korQuote = "오늘의 글",
        engQuote = engQuote,
        korAuthor = "작가",
        engAuthor = "Author",
        authorUrl = null,
    )
}
