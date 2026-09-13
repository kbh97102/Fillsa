package com.arakene.domain.usecase.common

import com.arakene.domain.repository.DailyNotificationPreferenceRepository
import com.arakene.domain.scheduler.DailyNotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyNotificationPreferenceUseCaseTest {

    @Test
    fun `enabling persists the preference and schedules daily notifications`() = runBlocking {
        val preference = FakePreference(false)
        val scheduler = RecordingScheduler()

        SetDailyNotificationEnabledUseCase(preference, scheduler)(true)

        assertTrue(preference.enabled)
        assertEquals(listOf(true), scheduler.enabledValues)
    }

    @Test
    fun `disabling persists the preference and cancels daily notifications`() = runBlocking {
        val preference = FakePreference(true)
        val scheduler = RecordingScheduler()

        SetDailyNotificationEnabledUseCase(preference, scheduler)(false)

        assertFalse(preference.enabled)
        assertEquals(listOf(false), scheduler.enabledValues)
    }

    @Test
    fun `startup reconciles the persisted disabled preference before scheduling`() = runBlocking {
        val preference = FakePreference(false)
        val scheduler = RecordingScheduler()

        RestoreDailyNotificationScheduleUseCase(preference, scheduler)()

        assertEquals(listOf(false), scheduler.enabledValues)
    }

    private class FakePreference(
        var enabled: Boolean
    ) : DailyNotificationPreferenceRepository {
        override suspend fun setAlarm(value: Boolean) {
            enabled = value
        }

        override fun getAlarm(): Flow<Boolean> = flowOf(enabled)
    }

    private class RecordingScheduler : DailyNotificationScheduler {
        val enabledValues = mutableListOf<Boolean>()

        override fun setEnabled(enabled: Boolean) {
            enabledValues += enabled
        }
    }
}
