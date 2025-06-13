package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.util.YN
import javax.inject.Inject

class GetLocalQuotePagingUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke(likeYN: YN) = localRepository.getLocalQuotesPaging(likeYN)

}