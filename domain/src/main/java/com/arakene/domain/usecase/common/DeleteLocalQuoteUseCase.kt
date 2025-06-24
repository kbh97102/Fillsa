package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class DeleteLocalQuoteUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(seq: Int) = localRepository.deleteQuote(seq)

}