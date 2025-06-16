package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleError
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.logDebug

@Composable
inline fun <reified VM : BaseViewModel> WithBaseErrorHandling(
    viewModel: BaseViewModel = hiltViewModel<VM>(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    crossinline logoutEvent: () -> Unit = {},
    content: @Composable () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    HandleError(
        viewModel.error,
        lifecycleOwner
    ) {
        when (it) {
            "404" -> {
                dialogDataHolder.apply {
                    data = DialogData.Builder().buildNetworkError(context, okOnClick = {
                        viewModel.lastContract?.let { it1 -> viewModel.handleContract(it1) }
                    })
                }.run {
                    show = true
                }
            }

            "401", "403" -> {
                dialogDataHolder.apply {
                    data = DialogData.Builder()
                        .title("로그인 시간이 만료되었습니다.\n재로그인해주세요")
                        .onClick {
                            logoutEvent()
                        }
                        .build()
                }.run {
                    show = true
                }
            }
        }
    }

    content()
}