package com.arakene.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * The persisted preference that controls the app's daily quote notification.
 */
interface DailyNotificationPreferenceRepository {
    suspend fun setAlarm(value: Boolean)
    fun getAlarm(): Flow<Boolean>
}
