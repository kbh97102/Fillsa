package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.util.YN
import javax.inject.Inject

class GetLocalQuotePagingUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke(
        likeYN: YN,
        startDate: String,
        endDate: String
    ) = localRepository.getLocalQuotesPaging(likeYN, startDate = startDate, endDate = endDate)

}