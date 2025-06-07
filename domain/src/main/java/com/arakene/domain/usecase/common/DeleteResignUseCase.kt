package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import javax.inject.Inject

class DeleteResignUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {

    suspend operator fun invoke() = commonRepository.deleteResign()

}