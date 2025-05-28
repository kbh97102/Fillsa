package com.arakene.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.usecase.LoginUseCase
import com.arakene.domain.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        when (val result = loginUseCase(loginRequest)) {
            is ApiResult.Success -> {
                Log.d(">>>>", "Success ${result.data}")
            }

            is ApiResult.Fail -> {
                Log.d(">>>>", "Fail ${result.error}")
            }

            is ApiResult.Error -> {
                Log.d(">>>>", "Error ${result.error}")
            }
        }

    }

}