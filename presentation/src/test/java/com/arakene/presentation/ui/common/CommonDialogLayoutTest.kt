package com.arakene.presentation.ui.common

import com.arakene.presentation.util.DialogLayoutMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonDialogLayoutTest {

    @Test
    fun `Home measured geometry is dark default-font only`() {
        assertTrue(shouldUseMeasuredHomeDialog(DialogLayoutMode.HomeDarkMeasured, darkMode = true, fontScale = 1f))
        assertFalse(shouldUseMeasuredHomeDialog(DialogLayoutMode.HomeDarkMeasured, darkMode = false, fontScale = 1f))
        assertFalse(shouldUseMeasuredHomeDialog(DialogLayoutMode.HomeDarkMeasured, darkMode = true, fontScale = 1.3f))
    }

    @Test
    fun `shared dialogs remain content driven`() {
        assertFalse(shouldUseMeasuredHomeDialog(DialogLayoutMode.Content, darkMode = true, fontScale = 1f))
    }
}
