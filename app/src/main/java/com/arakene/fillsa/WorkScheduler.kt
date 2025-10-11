package com.arakene.fillsa

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val MORNING_WORK = "DailyQuote_Morning"
    private const val NOON_WORK = "DailyQuote_Noon"

    fun scheduleDailyWorks(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val morningWork = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(6, 0), TimeUnit.MILLISECONDS)
            .addTag(MORNING_WORK)
            .build()

        val noonWork = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(12, 0), TimeUnit.MILLISECONDS)
            .addTag(NOON_WORK)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MORNING_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            morningWork
        )

        workManager.enqueueUniquePeriodicWork(
            NOON_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            noonWork
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
