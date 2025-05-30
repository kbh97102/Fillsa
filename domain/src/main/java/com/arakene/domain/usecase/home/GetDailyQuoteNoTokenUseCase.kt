package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class GetDailyQuoteNoTokenUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(quotaDate: String) =
        homeRepository.getDailyQuoteNoToken(quotaDate)

}