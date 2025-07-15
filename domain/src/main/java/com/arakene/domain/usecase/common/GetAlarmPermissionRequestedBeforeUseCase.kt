package com.arakene.domain.usecase.common

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class GetAlarmPermissionRequestedBeforeUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke() = localRepository.isAlarmPermissionRequestedBefore()

}