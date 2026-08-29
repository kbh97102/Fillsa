package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeQuoteSwipeTest {

    @Test
    fun `latest quote permits only a left swipe to the previous date`() {
        assertEquals(
            HomeQuoteSwipe.Previous,
            homeQuoteSwipe(horizontalDrag = -151f, canGoNext = false),
        )
        assertEquals(
            HomeQuoteSwipe.None,
            homeQuoteSwipe(horizontalDrag = 151f, canGoNext = false),
        )
    }
}
