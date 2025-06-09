package com.arakene.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arakene.presentation.R
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleError
import com.arakene.presentation.util.LocalDialogDataHolder

@Composable
inline fun <reified VM: BaseViewModel>WithBaseErrorHandling(
    viewModel: BaseViewModel = hiltViewModel<VM>(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    crossinline logoutEvent: () -> Unit = {},
    content: @Composable () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val isProcessing by remember {
        viewModel.isProcessing
    }

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

            "403" -> {
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


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        content()

        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = colorResource(R.color.purple01),
                trackColor = colorResource(R.color.purple02),
            )
        }
    }
}