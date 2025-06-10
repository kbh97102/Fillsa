package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetAlarmUsageUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(value: Boolean) = localRepository.setAlarm(value)

}