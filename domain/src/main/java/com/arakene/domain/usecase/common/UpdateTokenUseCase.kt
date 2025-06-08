package com.arakene.domain.usecase.common

import com.arakene.domain.repository.TokenRepository
import com.arakene.domain.requests.TokenRefreshRequest
import javax.inject.Inject

class UpdateTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {

    suspend operator fun invoke(request: TokenRefreshRequest) =
        repository.updateAccessToken(request)

}