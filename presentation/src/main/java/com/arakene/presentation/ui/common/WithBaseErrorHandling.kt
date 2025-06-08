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

@Composable
inline fun <reified VM: BaseViewModel>WithBaseErrorHandling(
    viewModel: BaseViewModel = hiltViewModel<VM>(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
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
        }
    }

    content()
}