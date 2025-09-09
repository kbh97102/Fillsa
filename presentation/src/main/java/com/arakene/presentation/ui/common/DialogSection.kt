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

            if (dialogData.drawableId < 0) {
                CommonDialog(
                    title = dialogData.title,
                    body = dialogData.body,
                    titleTextStyle = dialogData.titleTextStyle,
                    bodyTextStyle = dialogData.bodyTextStyle,
                    positiveText = dialogData.okText,
                    negativeText = dialogData.cancelText,
                    positiveOnClick = dialogData.onClick ?: {},
                    negativeOnClick = dialogData.cancelOnClick ?: {},
                    dismiss = {
                        dialogDataHolder.show = false
                    },
                    reversed = dialogData.reversed,
                    singleButton = dialogData.singleButton
                )
            } else {
                DialogWIthImage(
                    title = dialogData.title,
                    body = dialogData.body,
                    drawableId = dialogData.drawableId,
                    titleTextStyle = dialogData.titleTextStyle,
                    positiveText = dialogData.okText,
                    negativeText = dialogData.cancelText,
                    positiveOnClick = dialogData.onClick ?: {},
                    negativeOnClick = dialogData.cancelOnClick ?: {},
                    dismiss = {
                        dialogDataHolder.show = false
                    },
                    reversed = dialogData.reversed,
                    singleButton = dialogData.singleButton
                )
            }
        }
    }
}