package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.requests.LocalQuoteInfo
import javax.inject.Inject

class AddLocalQuoteUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(quoteInfo: LocalQuoteInfo) =
        localRepository.addLocalQuote(quoteInfo)
}