package com.arakene.domain.usecase.common

import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase
) {

    suspend operator fun invoke() {
        setAccessTokenUseCase("")
        setRefreshTokenUseCase("")
    }

}