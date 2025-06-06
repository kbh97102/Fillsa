package com.arakene.domain.usecase

import com.arakene.domain.repository.CommonRepository
import javax.inject.Inject

class GetNoticeUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) {

    operator fun invoke() = commonRepository.getNotice()

}