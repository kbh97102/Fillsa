package com.arakene.domain.usecase.list

import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.LikeRequest
import com.arakene.domain.requests.Pageable
import javax.inject.Inject

class GetQuotesListUseCase @Inject constructor(
    private val listRepository: ListRepository
) {

    suspend operator fun invoke(pageable: Pageable, request: LikeRequest) =
        listRepository.getQuotesList(pageable, request)

}