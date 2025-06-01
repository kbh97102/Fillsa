package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import javax.inject.Inject

class DeleteUploadImageUseCase @Inject constructor(
    private val repository: HomeRepository
) {

    suspend operator fun invoke(dailyQuoteSeq: Int) = repository.deleteUploadImage(dailyQuoteSeq)

}