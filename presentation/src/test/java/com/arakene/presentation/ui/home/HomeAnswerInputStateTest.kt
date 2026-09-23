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
    fun `family emoji and flag are each one visible grapheme`() {
        val family = "👨‍👩‍👧‍👦"
        val flag = "🇰🇷"
        assertEquals(family.repeat(200), homeAnswerInputState(family.repeat(200) + "x").text)
        assertEquals(198, homeAnswerInputState(family + flag).remainingCount)
    }

    @Test
    fun `draft and edit actions cannot change an in flight answer`() {
        val saving = HomeAnswerUiState(draft = "저장 중", isSaving = true)
        assertEquals(saving, changeHomeAnswer(saving, "다른 답변"))
        assertEquals(saving, editHomeAnswer(saving))
    }

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
