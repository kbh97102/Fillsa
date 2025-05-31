package com.arakene.domain.usecase.home

import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.requests.LikeRequest
import javax.inject.Inject

class PostLikeUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(likeRequest: LikeRequest, dailyQuoteSeq: Int) = homeRepository.postLike(likeRequest, dailyQuoteSeq)
}