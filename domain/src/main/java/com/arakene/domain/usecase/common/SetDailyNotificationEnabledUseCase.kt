package com.arakene.domain.usecase.common

import com.arakene.domain.repository.DailyNotificationPreferenceRepository
import com.arakene.domain.scheduler.DailyNotificationScheduler
import javax.inject.Inject

class SetDailyNotificationEnabledUseCase @Inject constructor(
    private val preferenceRepository: DailyNotificationPreferenceRepository,
    private val dailyNotificationScheduler: DailyNotificationScheduler
) {
    suspend operator fun invoke(enabled: Boolean) {
        preferenceRepository.setAlarm(enabled)
        dailyNotificationScheduler.setEnabled(enabled)
    }
}
