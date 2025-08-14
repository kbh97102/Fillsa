package com.arakene.domain.usecase

import com.arakene.domain.repository.AdRepository
import javax.inject.Inject

class GetAdStateUseCase @Inject constructor(
    private val repository: AdRepository
) {
    suspend operator fun invoke(useCache: Boolean = true) = repository.loadNativeAd(useCache)

}