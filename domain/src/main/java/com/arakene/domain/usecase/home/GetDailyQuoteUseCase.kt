package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class GetDailyQuoteUseCase @Inject constructor(
    private val homeRepository: HomeRepository
){

    suspend operator fun invoke(quoteDate: String) = homeRepository.getDailyQuote(quoteDate)

}