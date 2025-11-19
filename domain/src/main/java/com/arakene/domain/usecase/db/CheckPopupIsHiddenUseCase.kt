package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class CheckPopupIsHiddenUseCase @Inject constructor(private val localRepository: LocalRepository) {

    suspend operator fun invoke(seq: Int) = localRepository.checkPopupIsHidden(seq)

}