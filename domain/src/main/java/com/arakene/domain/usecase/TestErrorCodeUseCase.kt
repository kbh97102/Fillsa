package com.arakene.domain.usecase

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class TestErrorCodeUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(errorCode: Int) = homeRepository.testErrorCode(errorCode)

}