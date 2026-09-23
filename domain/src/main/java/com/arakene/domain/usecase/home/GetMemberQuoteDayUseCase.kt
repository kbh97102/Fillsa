package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class GetMemberQuoteDayUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(quoteDate: String) =
        homeRepository.getMemberQuoteDay(quoteDate)
}
