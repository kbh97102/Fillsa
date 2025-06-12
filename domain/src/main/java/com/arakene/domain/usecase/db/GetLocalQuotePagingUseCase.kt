package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class GetLocalQuotePagingUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke() = localRepository.getLocalQuotesPaging()

}