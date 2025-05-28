package com.arakene.domain.usecase

import com.arakene.domain.repository.LoginRepository
import com.arakene.domain.requests.LoginRequest
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest) = loginRepository.login(loginRequest)
}