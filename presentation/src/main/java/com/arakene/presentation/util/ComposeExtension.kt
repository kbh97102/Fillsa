package com.arakene.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.noEffectClickable(enable: Boolean = true, click: () -> Unit) = this.clickable(
    enabled = enable, onClick = click, interactionSource =
        remember { MutableInteractionSource() }, indication = null
)