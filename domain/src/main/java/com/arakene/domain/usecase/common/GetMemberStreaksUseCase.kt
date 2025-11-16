package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import javax.inject.Inject

class GetMemberStreaksUseCase @Inject constructor(private val repository: CommonRepository) {

    suspend operator fun invoke() = repository.getMemberStreaks()

}