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
fun TestDialog(
    generalPopup: PopupResponse?,
    clear: () -> Unit
) {

    var versionUpdateVisible by remember {
        mutableStateOf(false)
    }

    var eventVisible by remember {
        mutableStateOf(false)
    }

    var noticeVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(generalPopup) {
        val data = generalPopup ?: return@LaunchedEffect

        when(data.popupType){
            GeneralPopupType.VERSION_UPDATE.name -> {
                versionUpdateVisible = true
            }
            GeneralPopupType.EVENT.name -> {
                eventVisible = true
            }
            GeneralPopupType.NOTICE.name -> {
                noticeVisible = true
            }
        }

        clear()
    }

    ImageOnlyDialog(
        imageUri = generalPopup?.imageUrl ?: return,
        visible = versionUpdateVisible,
        onDismiss = {versionUpdateVisible = false}
    )

//    MainNoticeUpDialog(
//        title = generalPopup?.title ?: ""
//    )

    if (noticeVisible) {

    }

}