package com.arakene.domain.usecase.home

import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class SetImageUriUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(uri: String) = localRepository.setImageUri(uri)

}