package com.arakene.presentation.ui

import com.arakene.presentation.R
import com.arakene.presentation.util.Screens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BottomNavigationSpecTest {

    @Test
    fun `main navigation exposes the three Figma labels in order`() {
        assertEquals(
            listOf("Home", "Calendar", "My page"),
            mainBottomNavigationItems.map { it.screen.routeString },
        )
    }

    @Test
    fun `main navigation maps each Figma tab to its local icon`() {
        assertEquals(
            listOf(
                R.drawable.icn_bottom_menu_home,
                R.drawable.icn_bottom_menu_calendar,
                R.drawable.icn_bottom_menu_my_page,
            ),
            mainBottomNavigationItems.map { it.iconRes },
        )
    }

    @Test
    fun `main navigation is visible on its three root routes`() {
        assertTrue(shouldShowMainBottomNavigation(Screens.Home::class.qualifiedName))
        assertTrue(shouldShowMainBottomNavigation(Screens.Calendar::class.qualifiedName))
        assertTrue(shouldShowMainBottomNavigation(Screens.MyPage::class.qualifiedName))
    }

    @Test
    fun `quote list is not a main navigation route`() {
        assertFalse(shouldShowMainBottomNavigation(Screens.QuoteList::class.qualifiedName))
    }
}
