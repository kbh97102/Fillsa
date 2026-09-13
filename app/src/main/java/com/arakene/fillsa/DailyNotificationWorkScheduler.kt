package com.arakene.fillsa

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arakene.domain.scheduler.DailyNotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyNotificationWorkScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DailyNotificationScheduler {

    override fun setEnabled(enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (enabled) {
            workManager.enqueueUniquePeriodicWork(
                DAILY_NOTIFICATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                newDailyNotificationWork()
            )
        } else {
            workManager.cancelUniqueWork(DAILY_NOTIFICATION_WORK_NAME)
        }
    }

    private fun newDailyNotificationWork() = LocalDateTime.now().let { now ->
        val nextSixAm = now.toLocalDate().plusDays(1).atStartOfDay().withHour(6)
        PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(Duration.between(now, nextSixAm))
            .build()
    }

    companion object {
        const val DAILY_NOTIFICATION_WORK_NAME = "DailyNotificationWorker"
    }
}
