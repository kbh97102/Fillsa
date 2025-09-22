package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class GetDarkModeTypeUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    operator fun invoke() = localRepository.getDarkModeType()
}