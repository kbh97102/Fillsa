package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(token: String) = localRepository.setAccessToken(token)

}