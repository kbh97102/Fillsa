package com.arakene.domain.usecase.list

import com.arakene.domain.repository.ListRepository
import com.arakene.domain.requests.MemoRequest
import javax.inject.Inject

class PostSaveMemoUseCase @Inject constructor(
    private val listRepository: ListRepository
) {

    suspend operator fun invoke(memberQuoteSeq: String, request: MemoRequest) =
        listRepository.postSaveMemo(request = request, memberQuoteSeq = memberQuoteSeq)

}