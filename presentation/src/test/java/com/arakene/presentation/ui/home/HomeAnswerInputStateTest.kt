package com.arakene.presentation.ui.home

import com.arakene.presentation.util.homeAnswerInputState
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.changeHomeAnswer
import com.arakene.presentation.util.editHomeAnswer
import com.arakene.presentation.util.recordHomeAnswer
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

    @Test
    fun `recorded answer stays session-local and can be edited again`() {
        val recorded = recordHomeAnswer(changeHomeAnswer(HomeAnswerUiState(), "첫 답변"))

        assertEquals("첫 답변", recorded.recordedAnswer)
        assertEquals(true, recorded.isRecorded)
        assertEquals("첫 답변", editHomeAnswer(recorded).draft)
    }
}
