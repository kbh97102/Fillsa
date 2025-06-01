package com.arakene.domain.usecase.home

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class GetImageUriUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke() = localRepository.getImageUri()

}