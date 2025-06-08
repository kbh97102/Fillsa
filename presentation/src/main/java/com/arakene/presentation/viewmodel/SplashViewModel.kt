package com.arakene.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.arakene.domain.usecase.common.CheckFirstOpenUseCase
import com.arakene.presentation.util.Action
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkFirstOpenUseCase: CheckFirstOpenUseCase
) : BaseViewModel() {

    var ready = mutableStateOf(false)

    val destination = mutableStateOf<Screens>(Screens.Login)

    var permissionChecked = MutableStateFlow(false)


    fun checkReady() {
        viewModelScope.launch {
            checkFirstOpenUseCase().combine(permissionChecked) { firstOpen, permission ->
                Pair(firstOpen, permission)
            }.collectLatest {

                val firstOpen = it.first

                if (firstOpen) {
                    destination.value = Screens.Login
                } else {
                    destination.value = Screens.Home
                }

                ready.value = it.second
            }
        }
    }


    override fun handleAction(action: Action) {

    }
}