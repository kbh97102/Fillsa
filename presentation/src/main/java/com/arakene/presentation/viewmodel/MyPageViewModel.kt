package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arakene.domain.usecase.GetNoticeUseCase
import com.arakene.domain.usecase.common.DeleteResignUseCase
import com.arakene.domain.usecase.common.GetAlarmUsageUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.LogoutUseCase
import com.arakene.domain.usecase.common.SetAlarmUsageUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.CommonEffect
import com.arakene.presentation.util.MyPageAction
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getNoticeUseCase: GetNoticeUseCase,
    private val deleteResignUseCase: DeleteResignUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val setAlarmUsageUseCase: SetAlarmUsageUseCase,
    private val getAlarmUsageUseCase: GetAlarmUsageUseCase
) : BaseViewModel() {

    val isLogged = getLoginStatusUseCase()

    val getNotice = getNoticeUseCase()
        .cachedIn(viewModelScope)

    val getAlarmUsage = getAlarmUsageUseCase()

    override fun handleAction(action: Action) {
        when (val myPageAction = action as MyPageAction) {
            MyPageAction.Resign -> {
                resign()
            }

            MyPageAction.Logout -> {
                logout()
            }

            MyPageAction.Login -> {
                emitEffect(
                    CommonEffect.Move(
                        Screens.Login
                    )
                )
            }

            is MyPageAction.ClickAlarmUsage -> {
               updateAlarmUsage(myPageAction.usage)
            }

            else -> {

            }
        }
    }

    private fun updateAlarmUsage(usage: Boolean) {
        viewModelScope.launch {
            setAlarmUsageUseCase(usage)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    private fun resign() = viewModelScope.launch {
        deleteResignUseCase()
    }
}