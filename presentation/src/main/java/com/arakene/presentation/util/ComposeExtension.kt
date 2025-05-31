package com.arakene.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun Modifier.noEffectClickable(enable: Boolean = true, click: () -> Unit) = this.clickable(
    enabled = enable, onClick = click, interactionSource =
        remember { MutableInteractionSource() }, indication = null
)

@Composable
fun HandleViewEffect(
    effect: Flow<Effect>,
    lifecycleOwner: LifecycleOwner,
    compositionScope: CoroutineScope = rememberCoroutineScope(),
    effectHandler: suspend (Effect) -> Unit
) = LaunchedEffect(effect, lifecycleOwner) {

    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effect
                .onEach {
                    effectHandler(it)
                }
                .launchIn(compositionScope)
        }
    }
}