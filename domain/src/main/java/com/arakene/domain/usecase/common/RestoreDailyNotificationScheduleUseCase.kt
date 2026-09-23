package com.arakene.domain.usecase.common

import com.arakene.domain.repository.DailyNotificationPreferenceRepository
import com.arakene.domain.scheduler.DailyNotificationScheduler
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RestoreDailyNotificationScheduleUseCase @Inject constructor(
    private val preferenceRepository: DailyNotificationPreferenceRepository,
    private val dailyNotificationScheduler: DailyNotificationScheduler
) {
    suspend operator fun invoke() {
        dailyNotificationScheduler.setEnabled(preferenceRepository.getAlarm().first())
    }
}
