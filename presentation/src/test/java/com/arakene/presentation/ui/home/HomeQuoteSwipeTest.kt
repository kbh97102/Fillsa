package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeQuoteSwipeTest {

    @Test
    fun `right swipe selects previous and left swipe selects next when available`() {
        assertEquals(
            HomeQuoteSwipe.Previous,
            homeQuoteSwipe(horizontalDrag = 151f, canGoNext = true),
        )
        assertEquals(
            HomeQuoteSwipe.Next,
            homeQuoteSwipe(horizontalDrag = -151f, canGoNext = true),
        )
    }

    @Test
    fun `latest quote blocks only the left swipe to next`() {
        assertEquals(
            HomeQuoteSwipe.Previous,
            homeQuoteSwipe(horizontalDrag = 151f, canGoNext = false),
        )
        assertEquals(
            HomeQuoteSwipe.None,
            homeQuoteSwipe(horizontalDrag = -151f, canGoNext = false),
        )
    }
}
