package com.arakene.domain.usecase.common

import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.responses.MemberStreakResponse
import com.arakene.domain.util.ApiResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetMemberStreaksUseCase @Inject constructor(
    private val repository: CommonRepository,
    private val localRepository: LocalRepository
) {

    suspend operator fun invoke(): MemberStreakResponse? {
        val isLogged = localRepository.getLoginStatus().firstOrNull()
        return if (isLogged == true) {
            repository.getMemberStreaks().let {
                if (it is ApiResult.Success) {
                    it.data
                } else {
                    null
                }
            }
        } else {
            localRepository.getTodayLocalStreakInfo()?.let {
                MemberStreakResponse(
                    isTodayWritten = it.isDailyWritingCompleted,
                    currentStreak = it.streakDateCount
                )
            }
        }
    }

}