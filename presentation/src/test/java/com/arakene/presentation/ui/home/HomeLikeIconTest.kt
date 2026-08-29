package com.arakene.presentation.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLikeIconTest {

    @Test
    fun `like icon follows the persisted liked state`() {
        assertEquals(HomeLikeIcon.Selected, homeLikeIcon(isLiked = true))
        assertEquals(HomeLikeIcon.Unselected, homeLikeIcon(isLiked = false))
    }
}
