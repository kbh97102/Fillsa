package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.responses.DailyQuoteDto
import javax.inject.Inject

class GetLocalQuoteForWidgetUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke() = localRepository.getLocalQuoteForWidget()

}