package com.arakene.domain.usecase.home

import com.arakene.domain.repository.TypingRepository
import com.arakene.domain.requests.TypingQuoteRequest
import javax.inject.Inject

class PostTypingUseCase @Inject constructor(
    private val typingRepository: TypingRepository
) {

    suspend operator fun invoke(dailyQuoteSeq: Int, request: TypingQuoteRequest) =
        typingRepository.postTyping(dailyQuoteSeq, request)

}