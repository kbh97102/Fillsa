package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.requests.AnswerRequest
import javax.inject.Inject

class SaveQuoteAnswerUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(dailyQuoteSeq: Int, answer: String) =
        homeRepository.postAnswer(
            dailyQuoteSeq = dailyQuoteSeq,
            request = AnswerRequest(answer)
        )
}
