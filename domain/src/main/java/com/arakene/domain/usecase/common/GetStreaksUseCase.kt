package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import javax.inject.Inject

class GetStreaksUseCase @Inject constructor(private val repository: CommonRepository) {

    suspend operator fun invoke() = repository.getStreaks()

}