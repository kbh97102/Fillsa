package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetStreakCountUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val commonRepository: CommonRepository
) {

    suspend operator fun invoke(): Int {

        val isLogged = localRepository.getLoginStatus().firstOrNull() ?: false

        return if (isLogged) {
            commonRepository.getMemberStreaks().let {
                if (it is ApiResult.Success) {
                    it.data.currentStreak
                } else {
                    0
                }
            }
        } else {
            localRepository.getStreakDateCount()
        }
    }

}