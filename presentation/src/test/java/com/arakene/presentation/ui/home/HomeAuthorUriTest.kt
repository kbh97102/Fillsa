package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeAuthorUriTest {

    @Test
    fun `author search uses the existing Wikipedia URI format`() {
        assertEquals(
            "https://wikipedia.org/wiki/John_Wooden",
            homeAuthorUri("  John Wooden  "),
        )
    }
}
