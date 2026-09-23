package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class GetMemberWeeklyQuotesUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(endDate: String? = null) =
        homeRepository.getWeeklyQuotes(endDate)
}
