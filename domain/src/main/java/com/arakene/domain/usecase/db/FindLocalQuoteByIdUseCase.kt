package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class FindLocalQuoteByIdUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(seq: Int) = localRepository.findLocalQuoteById(seq)

}