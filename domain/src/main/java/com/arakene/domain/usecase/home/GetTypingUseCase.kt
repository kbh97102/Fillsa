package com.arakene.domain.usecase.home

import com.arakene.domain.repository.TypingRepository
import javax.inject.Inject

class GetTypingUseCase @Inject constructor(
    private val typingRepository: TypingRepository
) {

    suspend operator fun invoke(dailyQuoteSeq: Int) =
        typingRepository.getTyping(dailyQuoteSeq)

}