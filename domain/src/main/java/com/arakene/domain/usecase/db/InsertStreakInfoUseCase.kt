package com.arakene.domain.usecase.db

import com.arakene.domain.model.StreakInfo
import com.arakene.domain.repository.LocalRepository
import javax.inject.Inject

class InsertStreakInfoUseCase @Inject constructor(private val localRepository: LocalRepository) {

    suspend operator fun invoke(streakInfo: StreakInfo) =
        localRepository.setStreakInfo(info = streakInfo)

}