package com.arakene.domain.usecase.db

import com.arakene.domain.model.PromptAnswerRecord
import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SavePromptAnswerUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend operator fun invoke(record: PromptAnswerRecord) = localRepository.savePromptAnswer(record)
}
