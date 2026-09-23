package com.arakene.presentation.ui.home

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShareLayoutTest {

    @Test
    fun `dark pager keeps only the Figma maximum`() {
        assertEquals(481.dp, sharePagerMaxHeight(darkMode = true))
    }

    @Test
    fun `light pager has no dark cap`() {
        assertNull(sharePagerMaxHeight(darkMode = false))
    }
}
