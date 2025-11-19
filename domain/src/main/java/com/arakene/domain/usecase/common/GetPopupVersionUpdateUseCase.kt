package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import javax.inject.Inject

class GetPopupVersionUpdateUseCase @Inject constructor(private val commonRepository: CommonRepository) {

    suspend operator fun invoke() = commonRepository.getPopUpVersionUpdate()

}