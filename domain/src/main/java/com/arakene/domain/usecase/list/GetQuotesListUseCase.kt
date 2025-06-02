package com.arakene.domain.usecase.list

import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import javax.inject.Inject

class GetQuotesListUseCase @Inject constructor(
    private val listRepository: ListRepository
) {

    operator fun invoke(request: LikeRequest) =
        listRepository.getQuotesList(request)

}