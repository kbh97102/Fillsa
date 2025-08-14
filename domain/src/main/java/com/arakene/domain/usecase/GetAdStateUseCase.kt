package com.arakene.domain.usecase

import com.arakene.domain.repository.AdRepository
import javax.inject.Inject

class GetAdStateUseCase @Inject constructor(
    private val repository: AdRepository
) {
    operator fun invoke() = repository.getAdStateFlow()

}