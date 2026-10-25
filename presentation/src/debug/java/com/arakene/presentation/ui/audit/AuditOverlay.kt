package com.arakene.presentation.ui.audit

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier

/** Preview-only window approximation; dialog content is the production component. */
@Composable
internal fun AuditOverlay(background: @Composable () -> Unit = {}, dim: Float = 0.6f, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
        background()
        androidx.compose.foundation.layout.Box(Modifier.matchParentSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = dim)), contentAlignment = androidx.compose.ui.Alignment.Center) { content() }
    }
}
