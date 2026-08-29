package com.arakene.presentation.ui.home

import com.arakene.presentation.util.homeAnswerInputState
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeAnswerInputStateTest {

    @Test
    fun `answer input caps at two hundred grapheme clusters and reports remaining count`() {
        val grapheme = "a\u0301"

        val state = homeAnswerInputState(grapheme.repeat(200) + "x")

        assertEquals(grapheme.repeat(200), state.text)
        assertEquals(0, state.remainingCount)
    }
}
