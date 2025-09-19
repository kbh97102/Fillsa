package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.util.DarkModeType
import javax.inject.Inject

class SetDarkModeTypeUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(darkModeType: DarkModeType) = localRepository.setDarkModeType(darkModeType)
}