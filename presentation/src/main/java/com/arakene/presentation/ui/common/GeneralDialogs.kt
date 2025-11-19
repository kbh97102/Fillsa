package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arakene.domain.responses.PopupResponse
import com.arakene.presentation.util.GeneralPopupType

@Composable
fun GeneralDialogs(
    generalPopup: PopupResponse?,
    getNextPopUp: () -> Unit,
    addHiddenPopUp: (Int) -> Unit
) {

    var versionUpdateVisible by remember {
        mutableStateOf(false)
    }

    var noticeVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(generalPopup) {
        val data = generalPopup ?: return@LaunchedEffect

        when (data.popupType) {
            GeneralPopupType.VERSION_UPDATE.name -> {
                versionUpdateVisible = true
            }

            GeneralPopupType.NOTICE.name, GeneralPopupType.EVENT.name -> {
                noticeVisible = true
            }
        }
    }

    if (versionUpdateVisible) {
        ImageOnlyDialog(
            imageUri = generalPopup?.imageUrl ?: return,
            visible = versionUpdateVisible,
            onDismiss = {
                versionUpdateVisible = false
                getNextPopUp()
            }
        )
    }

    if (noticeVisible) {

        when {
            generalPopup?.imageUrl?.isBlank() == false -> {
                ImageOnlyDialog(
                    imageUri = generalPopup.imageUrl ?: return,
                    visible = noticeVisible,
                    onDismiss = {
                        noticeVisible = false
                        getNextPopUp()
                    }
                )
            }

            else -> {
                MainNoticeUpDialog(
                    title = generalPopup?.title ?: return,
                    visible = noticeVisible,
                    message = generalPopup.content ?: "",
                    onDismiss = {
                        noticeVisible = false
                        getNextPopUp()
                    },
                    onDismissToday = {
                        addHiddenPopUp(generalPopup.popupSeq)
                        noticeVisible = false
                        getNextPopUp()
                    }
                )
            }
        }
    }


}