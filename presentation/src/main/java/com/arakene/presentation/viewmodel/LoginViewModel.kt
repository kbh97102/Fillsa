package com.arakene.presentation.viewmodel

import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arakene.domain.requests.LoginData
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.requests.TokenData
import com.arakene.domain.requests.UserData
import com.arakene.domain.usecase.LoginUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.util.BaseViewModel
import com.arakene.presentation.util.Effect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {



    fun login(
        accessToken: String?,
        refreshToken: String?,
        id: String?,
        refreshTokenExpiresIn: String? = null,
        expiresIn: String? = null,
        testMethod: () -> Unit
    ) = viewModelScope.launch {

        val request = LoginRequest(
            LoginData(
                oAuthProvider = "google",
                tokenData = TokenData(
                    deviceId = Settings.Secure.ANDROID_ID,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    refreshTokenExpiresIn = refreshTokenExpiresIn,
                    expiresIn = expiresIn
                ),
                userData = UserData(
                    id = id ?: "",
                    nickname = "",
                    profileImageUrl = ""
                )
            ),
            syncData = null
        )

        when (val result = loginUseCase(request)) {
            is ApiResult.Success -> {
                Log.d(">>>>", "Success ${result.data}")
                testMethod()
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