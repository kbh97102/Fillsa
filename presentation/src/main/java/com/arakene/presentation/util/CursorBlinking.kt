package com.arakene.presentation.util

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue

fun Modifier.cursorBlinking(
    value: TextFieldValue,
    layoutResult: TextLayoutResult?
) = composed {

    val state = remember { CursorAnimateState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(value.annotatedString) {
        state.startBlinking()
    }

    drawWithContent {
        this.drawContent()

        val cursorIndex = value.selection.start
        val result = layoutResult ?: return@drawWithContent

        val alpha = state.cursorAlpha

        if (result.getLineForOffset(cursorIndex) != -1 && alpha > 0) {
            val cursorOffset = result.getCursorRect(cursorIndex)
            drawLine(
                color = Color.Black,
                start = cursorOffset.topLeft,
                end = cursorOffset.bottomLeft,
                alpha = state.cursorAlpha
            )

        }
    }

}