package com.arakene.presentation.ui.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypingDarkModeTest {

    @Test
    fun `Figma handwriting decoration is dark-only`() {
        assertTrue(typingDecorationVisible(darkMode = true))
        assertFalse(typingDecorationVisible(darkMode = false))
    }
}
