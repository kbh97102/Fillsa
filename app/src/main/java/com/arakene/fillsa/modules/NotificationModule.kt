package com.arakene.fillsa.modules

import com.arakene.domain.scheduler.DailyNotificationScheduler
import com.arakene.fillsa.DailyNotificationWorkScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun bindDailyNotificationScheduler(
        scheduler: DailyNotificationWorkScheduler
    ): DailyNotificationScheduler
}
