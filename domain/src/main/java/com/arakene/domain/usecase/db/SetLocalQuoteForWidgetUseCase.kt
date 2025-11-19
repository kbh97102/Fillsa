package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import javax.inject.Inject

class SetLocalQuoteForWidgetUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(data: DailyQuotaNoToken) = localRepository.setLocalQuoteForWidget(data)

}