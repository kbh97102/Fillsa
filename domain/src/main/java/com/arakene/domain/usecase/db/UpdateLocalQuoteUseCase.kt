package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.requests.LocalQuoteInfo
import javax.inject.Inject

class UpdateLocalQuoteUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(quote: LocalQuoteInfo) = localRepository.updateQuote(quote)
}