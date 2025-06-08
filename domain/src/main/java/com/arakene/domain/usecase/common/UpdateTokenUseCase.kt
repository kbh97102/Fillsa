package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.requests.TokenRefreshRequest
import javax.inject.Inject

class UpdateTokenUseCase @Inject constructor(
    private val repository: CommonRepository
) {

    suspend operator fun invoke(request: TokenRefreshRequest) =
        repository.updateAccessToken(request)

}