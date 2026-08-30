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
                loadState = HomeQuoteLoadState.Loading,
            ),
        )
    }

    @Test
    fun `empty default quote cannot navigate to typing`() {
        assertNull(
            homeTypingDestination(
                quote = DailyQuoteDto(),
                loadState = HomeQuoteLoadState.Loaded,
            ),
        )
    }

    @Test
    fun `english-only payload cannot navigate to Korean-first typing`() {
        assertNull(
            homeTypingDestination(
                quote = loadedQuote(korQuote = "", engQuote = "English only"),
                loadState = HomeQuoteLoadState.Loaded,
            ),
        )
    }

    @Test
    fun `loaded Korean quote navigates to Korean-first typing with its original sequence`() {
        val destination = homeTypingDestination(
            quote = loadedQuote(),
            loadState = HomeQuoteLoadState.Loaded,
        )

        assertEquals(73, destination?.dailyQuoteDto?.dailyQuoteSeq)
        assertEquals("오늘의 글", destination?.dailyQuoteDto?.korQuote)
    }

    private fun loadedQuote(
        korQuote: String = "오늘의 글",
        engQuote: String = "Today's writing",
    ) = DailyQuoteDto(
        likeYn = "N",
        imagePath = null,
        dailyQuoteSeq = 73,
        korQuote = korQuote,
        engQuote = engQuote,
        korAuthor = "작가",
        engAuthor = "Author",
        authorUrl = null,
    )
}
