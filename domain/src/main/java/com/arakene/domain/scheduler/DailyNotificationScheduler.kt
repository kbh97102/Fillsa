package com.arakene.domain.scheduler

/**
 * Owns the platform-specific daily notification work lifecycle.
 */
interface DailyNotificationScheduler {
    fun setEnabled(enabled: Boolean)
}
