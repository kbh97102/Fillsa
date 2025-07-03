package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.usecase.common.CheckFirstOpenUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.LogoutUseCase
import com.arakene.domain.usecase.common.SetAlarmUsageUseCase
import com.arakene.domain.usecase.common.SetFirstOpenUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.AlarmManagerHelper
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.Screens
import com.arakene.presentation.util.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkFirstOpenUseCase: CheckFirstOpenUseCase,
    private val setFirstOpenUseCase: SetFirstOpenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val setAlarmUsageUseCase: SetAlarmUsageUseCase,
    private val alarmManagerHelper: AlarmManagerHelper
) : BaseViewModel() {

    val isLogged = getLoginStatusUseCase()

    var ready = MutableStateFlow(false)

    var destination: Screens = Screens.Login(isOnBoarding = false)

    var permissionChecked = MutableStateFlow(false)
    var hasPlayedOnce = MutableStateFlow(false)

    fun setAlarm() = alarmManagerHelper.setAlarm()
    fun cancelAlarm() = alarmManagerHelper.cancelAlarm()

    fun clearToken() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun setAlarmUsage(usage: Boolean) {
        viewModelScope.launch {
            setAlarmUsageUseCase(usage)
        }
    }

    fun checkReady() {
        viewModelScope.launch {
            val firstOpen = checkFirstOpenUseCase().first()

            combine(permissionChecked, hasPlayedOnce, ::Pair)
                .collectLatest {
                    val (checked, animation) = it

                    if (checked && animation) {
                        if (firstOpen) {
                            setFirstOpenUseCase()
                            destination = Screens.Login(isOnBoarding = false)
                        } else {
                            destination = Screens.Home()
                        }
                        ready.value = true
                    }
                }

        }
    }

    override fun handleAction(action: Action) {

    }
}