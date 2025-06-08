package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class CheckFirstOpenUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke()= repository.isFirstOpen()
}
