package com.arakene.presentation.util

import android.content.ClipData
import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arakene.presentation.R
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

val LocalSnackbarHost = compositionLocalOf { SnackbarHostState() }


fun copyToClipboard(
    context: Context,
    scope: CoroutineScope,
    clipBoard: Clipboard,
    snackbarHostState: SnackbarHostState,
    quote: String,
    author: String
) {
    scope.launch {
        val copyText = "$quote - $author"
        clipBoard.setClipEntry(ClipEntry(ClipData.newPlainText(copyText, copyText)))

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            snackbarHostState.showSnackbar(context.getString(R.string.copied))
        }
    }
}