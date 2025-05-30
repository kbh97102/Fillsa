package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetRefreshTokenUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(token: String) = localRepository.setRefreshToken(token)

}