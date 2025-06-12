package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class EmitTokenExpiredUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(value: String) = localRepository.emitTokenExpired(value)

}