package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arakene.presentation.util.DialogDataHolder

@Composable
fun DialogSection(
    dialogDataHolder: DialogDataHolder,
) {

    val data by remember(dialogDataHolder.data) {
        mutableStateOf(dialogDataHolder.data)
    }

    if (dialogDataHolder.show) {
        data?.let { dialogData ->
            CommonDialog(
                title = dialogData.title,
                body = dialogData.body,
                titleTextSize = dialogData.titleTextSize,
                bodyTextSize = dialogData.bodyTextSize,
                positiveText = dialogData.okText,
                negativeText = dialogData.cancelText,
                positiveOnClick = dialogData.onClick ?: {},
                negativeOnClick = dialogData.cancelOnClick ?: {},
                dismiss = {
                    dialogDataHolder.show = false
                },
                reversed = dialogData.reversed
            )
        }
    }
}