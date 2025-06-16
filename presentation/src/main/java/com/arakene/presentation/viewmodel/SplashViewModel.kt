package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.usecase.common.CheckFirstOpenUseCase
import com.arakene.domain.usecase.common.GetLoginStatusUseCase
import com.arakene.domain.usecase.common.GetTokenExpiredUseCase
import com.arakene.domain.usecase.common.LogoutUseCase
import com.arakene.domain.usecase.common.SetFirstOpenUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkFirstOpenUseCase: CheckFirstOpenUseCase,
    private val setFirstOpenUseCase: SetFirstOpenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val getTokenExpiredUseCase: GetTokenExpiredUseCase
) : BaseViewModel() {

    val isLogged = getLoginStatusUseCase()

    var ready = MutableStateFlow(false)

    val destination = mutableStateOf<Screens>(Screens.Login(isOnBoarding = false))

    var permissionChecked = MutableStateFlow(false)

    fun clearToken() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun checkReady() {
        viewModelScope.launch {
            val firstOpen = checkFirstOpenUseCase().first()
            val checked = permissionChecked.filter { it }.first()

            if (checked) {
                if (firstOpen) {
                    setFirstOpenUseCase()
                    destination.value = Screens.Login(isOnBoarding = false)
                } else {
                    destination.value = Screens.Home()
                }
                ready.value = true
            }
        }
    }

    suspend fun waitUntilReady() = ready.filter { it }.first()

    override fun handleAction(action: Action) {

    }
}