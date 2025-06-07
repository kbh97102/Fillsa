package com.arakene.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleError
import com.arakene.presentation.util.LocalDialogDataHolder

@Composable
fun WithBaseErrorHandling(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit,
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current
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