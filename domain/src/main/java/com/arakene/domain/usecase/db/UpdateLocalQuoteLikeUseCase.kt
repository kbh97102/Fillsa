package com.arakene.domain.usecase.db

import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.util.YN
import javax.inject.Inject

class UpdateLocalQuoteLikeUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(likeYN: YN, seq: Int) =
        localRepository.updateLocalQuoteLike(likeYN, seq)
}