package com.arakene.presentation.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.arakene.domain.util.CommonError
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.DialogData
import com.arakene.presentation.util.DialogDataHolder
import com.arakene.presentation.util.HandleError
import com.arakene.presentation.util.LocalDialogDataHolder
import com.arakene.presentation.util.LocalMoveHolder
import com.arakene.presentation.util.LocalSnackbarHost
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.TypographyEnum
import com.arakene.presentation.util.showCustomSnackbar
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
inline fun <reified VM : BaseViewModel> WithBaseErrorHandling(
    viewModel: BaseViewModel = hiltViewModel<VM>(),
    dialogDataHolder: DialogDataHolder = LocalDialogDataHolder.current,
    snackBar: SnackbarHostState = LocalSnackbarHost.current,
    moveScreen: NavHostController? = LocalMoveHolder.current,
    crossinline logoutEvent: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var displayUpdateDialog by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    HandleError(
        viewModel.error,
        lifecycleOwner
    ) {
        when (it) {
            is CommonError.ApiFail -> {
                when (it.errorResponse.errorCode) {

                    1005, 1006 -> {
                        dialogDataHolder.apply {
                            data = DialogData.Builder()
                                .singleButton(true)
                                .title("일시적인 오류가 발생했어요.")
                                .titleTextStyle(TypographyEnum.Heading4)
                                .body("잠시 후 다시 시도해 주세요.")
                                .bodyTextStyle(TypographyEnum.Body2)
                                .okText("확인")
                                .build()
                        }.run {
                            show = true
                        }
                    }

                    1002 -> {
                        scope.launch {
                            snackBar.showCustomSnackbar("탈퇴 처리된 계정이에요.", displayIcon = false)
                        }
                        moveScreen?.navigate(Screens.Login())
                    }

                    1010 -> {
                        dialogDataHolder.apply {
                            data = DialogData.Builder()
                                .singleButton(true)
                                .title("현재 서비스 점검 중입니다.\n잠시 후 다시 이용해 주세요.")
                                .okText("확인")
                                .onClick { exitProcess(0) }
                                .build()
                        }.run {
                            show = true
                        }
                    }

                    1007 -> {
                        displayUpdateDialog = true
                    }

                    // 서버 커스텀 에러 코드
                    1999 -> {
                        dialogDataHolder.apply {
                            data = DialogData.Builder()
                                .singleButton(true)
                                .title(it.errorResponse.message.ifEmpty { "요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요." })
                                .build()
                        }.run {
                            show = true
                        }
                    }

                    401, 403 -> {
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

                    404 -> {
                        dialogDataHolder.apply {
                            data = DialogData.Builder().buildNetworkError(
                                context, okOnClick = {
                                    viewModel.lastContract?.let { it1 ->
                                        viewModel.handleContract(
                                            it1
                                        )
                                    }
                                },
                                cancelOnClick = {
                                    exitProcess(0)
                                })
                        }.run {
                            show = true
                        }
                    }

                    else -> {
                        snackBar.showCustomSnackbar("요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.", displayIcon = false)
                    }
                }
            }

            is CommonError.NetworkError -> {
                dialogDataHolder.apply {
                    data = DialogData.Builder().buildNetworkError(
                        context, okOnClick = {
                            viewModel.lastContract?.let { it1 -> viewModel.handleContract(it1) }
                        },
                        cancelOnClick = {
                            exitProcess(0)
                        }
                    )
                }.run {
                    show = true
                }
            }

            else -> {
                dialogDataHolder.apply {
                    data = DialogData.Builder()
                        .title("요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.")
                        .build()
                }.run {
                    show = true
                }
            }
        }
    }

    if (displayUpdateDialog) {
        UpdateDialog(
            onDismiss = {
                displayUpdateDialog = false
            }
        )
    }

    content()
}
