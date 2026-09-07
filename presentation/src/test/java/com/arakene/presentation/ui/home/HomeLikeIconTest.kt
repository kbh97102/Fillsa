package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLikeIconTest {

    @Test
    fun `like icon follows the persisted liked state`() {
        assertEquals(HomeLikeIcon.Selected, homeLikeIcon(isLiked = true))
        assertEquals(HomeLikeIcon.Unselected, homeLikeIcon(isLiked = false))
    }

    @Test
    fun `image action label follows the registered image state`() {
        assertEquals(HomeImageActionPresentation.Register, homeImageActionPresentation(""))
        assertEquals(HomeImageActionPresentation.View, homeImageActionPresentation("https://example.test/image.png"))
    }
}
