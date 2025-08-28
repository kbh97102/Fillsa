package com.arakene.domain.usecase.list

import com.arakene.domain.repository.ListRepository
import javax.inject.Inject

class GetQuotesListUseCase @Inject constructor(
    private val listRepository: ListRepository
) {

    operator fun invoke(
        likeYn: String,
        startDate: String,
        endDate: String
    ) =
        listRepository.getQuotesList(likeYn, startDate = startDate, endDate = endDate)

}