package com.arakene.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.arakene.domain.repository.InstallationIdProvider
import com.arakene.domain.repository.LoginRepository
import com.arakene.domain.requests.LoginRequest
import com.arakene.domain.responses.LoginResponse
import com.arakene.domain.usecase.LoginUseCase
import com.arakene.domain.usecase.common.SetAccessTokenUseCase
import com.arakene.domain.usecase.common.SetRefreshTokenUseCase
import com.arakene.domain.usecase.common.SetUserNameUseCase
import com.arakene.domain.usecase.db.ClearLocalDataUseCase
import com.arakene.domain.usecase.db.GetLocalQuoteListUseCase
import com.arakene.domain.usecase.home.SetImageUriUseCase
import com.arakene.domain.util.ApiResult
import com.arakene.presentation.util.action.LoginAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginDeviceIdTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private var viewModel: LoginViewModel? = null

    @After fun tearDown() { viewModel?.viewModelScope?.cancel() }

    @Test fun loginSendsFirebaseInstallationId() = runTest {
        val requests = mutableListOf<LoginRequest>()
        val vm = createViewModel("fid-test-123", requests)

        runCurrent()
        vm.handleContract(googleLoginAction())
        advanceUntilIdle()

        assertEquals("fid-test-123", requests.single().loginData.deviceData.deviceId)
        assertEquals("ANDROID", requests.single().loginData.deviceData.osType)
    }

    @Test fun loginDoesNotSendEmptyInstallationId() = runTest {
        val requests = mutableListOf<LoginRequest>()
        val vm = createViewModel("", requests)

        runCurrent()
        vm.handleContract(googleLoginAction())
        advanceUntilIdle()

        assertTrue(requests.isEmpty())
    }

    @Test fun loginActionRemainsAvailableForRetryAfterInstallationIdFailure() = runTest {
        val requests = mutableListOf<LoginRequest>()
        var attempts = 0
        val provider = object : InstallationIdProvider {
            override suspend fun getId(): String = if (++attempts == 1) "" else "fid-test-123"
        }
        val vm = createViewModel(provider, requests)

        runCurrent()
        val loginAction = googleLoginAction()
        vm.handleContract(loginAction)
        advanceUntilIdle()
        assertTrue(requests.isEmpty())
        assertEquals(1, attempts)
        assertEquals(loginAction, vm.lastContract)
    }

    private fun createViewModel(id: String, requests: MutableList<LoginRequest>): LoginViewModel {
        return createViewModel(
            object : InstallationIdProvider { override suspend fun getId() = id }, requests
        )
    }

    private fun createViewModel(provider: InstallationIdProvider, requests: MutableList<LoginRequest>): LoginViewModel {
        val local = CountingLocalRepository(loggedIn = false)
        val loginRepository = object : LoginRepository {
            override suspend fun login(loginRequest: LoginRequest): ApiResult<LoginResponse> {
                requests += loginRequest
                return ApiResult.Success(LoginResponse("access-test", "refresh-test", 1, "tester", ""))
            }
        }
        return LoginViewModel(
            LoginUseCase(loginRepository),
            SetRefreshTokenUseCase(local),
            SetAccessTokenUseCase(local),
            SetImageUriUseCase(local),
            SetUserNameUseCase(local),
            GetLocalQuoteListUseCase(local),
            ClearLocalDataUseCase(local),
            provider
        ).also { viewModel = it }
    }

    private fun googleLoginAction() = LoginAction.ClickGoogleLogin(
        accessToken = null,
        refreshToken = null,
        idToken = null,
        accessTokenExpirationTime = null,
        appVersion = "1.0.30",
        isOnboarding = false
    )
}
