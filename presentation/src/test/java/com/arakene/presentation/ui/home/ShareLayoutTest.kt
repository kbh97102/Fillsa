package com.arakene.presentation.ui.home

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareLayoutTest {

    @Test
    fun `compact viewport reserves room for all share controls`() {
        assertEquals(477.dp, sharePagerHeight(629.dp, 24.dp))
    }

    @Test
    fun `tall viewport caps pager at Figma height`() {
        assertEquals(481.dp, sharePagerHeight(730.dp, 24.dp))
    }
}
