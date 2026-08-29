package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class GetPromptAnswerUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend operator fun invoke(date: String, question: String) =
        localRepository.getPromptAnswer(date, question)
}
