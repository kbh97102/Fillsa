package com.arakene.presentation.ui.home

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeColorPaletteTest {

    @Test
    fun `dark palette resolves the Figma Home dark tokens`() {
        val palette = homeColorPalette(darkMode = true)

        assertEquals(Color(0xFF212121), palette.background)
        assertEquals(Color(0xFF424242), palette.card)
        assertEquals(Color(0xFF616161), palette.cardBorder)
        assertEquals(Color(0x8C616161), palette.mainDivider)
        assertEquals(Color.White, palette.primaryText)
        assertEquals(Color(0xFFE0E0E0), palette.actionLabel)
        assertEquals(Color(0xFF9E9E9E), palette.mutedText)
        assertEquals(Color(0xFF424242), palette.answerField)
        assertEquals(Color(0xFF616161), palette.answerBorder)
    }

    @Test
    fun `light palette preserves the approved Home Figma colors`() {
        val palette = homeColorPalette(darkMode = false)

        assertEquals(Color(0xFFFFEFCC), palette.background)
        assertEquals(Color(0xFFFFF7E6), palette.card)
        assertEquals(Color(0xFF212121), palette.primaryText)
    }
}
