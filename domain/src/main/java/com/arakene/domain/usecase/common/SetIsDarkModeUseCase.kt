package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetIsDarkModeUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(isDarkMode: Boolean) = localRepository.setIsDarkMode(isDarkMode)
}