package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.requests.LocalQuoteInfo
import javax.inject.Inject

class UpdateLocalQuoteMemoUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(memo: String, seq: Int) = localRepository.updateLocalQuoteMemo(memo, seq)
}