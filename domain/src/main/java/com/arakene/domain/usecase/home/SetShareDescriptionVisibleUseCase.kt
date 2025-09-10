package com.arakene.domain.usecase.home

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetShareDescriptionVisibleUseCase @Inject constructor(private val localRepository: LocalRepository) {

    suspend operator fun invoke(boolean: Boolean) =
        localRepository.setShareDescriptionVisible(boolean)

}