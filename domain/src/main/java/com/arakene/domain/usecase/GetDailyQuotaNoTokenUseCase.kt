package com.arakene.domain.usecase

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class GetDailyQuotaNoTokenUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(quotaDate: String) =
        homeRepository.getDailyQuotaNoToken(quotaDate)

}