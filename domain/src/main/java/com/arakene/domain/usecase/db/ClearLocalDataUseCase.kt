package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class ClearLocalDataUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke() = localRepository.clear()

}