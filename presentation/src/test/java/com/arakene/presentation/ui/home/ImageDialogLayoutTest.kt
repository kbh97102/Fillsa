package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageDialogLayoutTest {

    @Test
    fun `dark text uses bounded scroll region between controls`() {
        assertEquals(ImageDialogTextLayout.BoundedScrollable, imageDialogTextLayout(darkMode = true))
    }

    @Test
    fun `light text keeps intrinsic baseline layout`() {
        assertEquals(ImageDialogTextLayout.Intrinsic, imageDialogTextLayout(darkMode = false))
    }
}
