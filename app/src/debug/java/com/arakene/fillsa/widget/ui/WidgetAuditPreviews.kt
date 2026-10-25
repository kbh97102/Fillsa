package com.arakene.fillsa.widget.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.IsDarkMode
import kotlinx.coroutines.flow.MutableStateFlow

/** Preview-only storage. Never reads or changes installed widget preferences. */
private class AuditWidgetStore : DataStore<Preferences> {
    override val data = MutableStateFlow(emptyPreferences())
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
        transform(data.value).also { data.value = it }
}

@Composable
private fun WidgetAuditTheme(dark: Boolean, content: @Composable () -> Unit) = FillsaTheme(darkTheme = dark) {
    CompositionLocalProvider(IsDarkMode provides dark, content = content)
}

@Preview(widthDp = 360, heightDp = 720, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun WidgetConfigLightAudit() = WidgetAuditTheme(false) {
    MyWidgetConfigScreen({}, remember { AuditWidgetStore() })
}

@Preview(widthDp = 360, heightDp = 720, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun WidgetConfigDarkAudit() = WidgetAuditTheme(true) {
    MyWidgetConfigScreen({}, remember { AuditWidgetStore() })
}

@Preview(widthDp = 109, heightDp = 115, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun Widget2x2LightAudit() = WidgetAuditTheme(false) { WidgetPreview2x2(true) }

@Preview(widthDp = 109, heightDp = 115, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun Widget2x2DarkAudit() = WidgetAuditTheme(true) { WidgetPreview2x2(true) }

@Preview(widthDp = 245, heightDp = 115, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun Widget4x2LightAudit() = WidgetAuditTheme(false) { WidgetPreview4x2(true) }

@Preview(widthDp = 245, heightDp = 115, locale = "ko", fontScale = 1f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun Widget4x2DarkAudit() = WidgetAuditTheme(true) { WidgetPreview4x2(true) }
