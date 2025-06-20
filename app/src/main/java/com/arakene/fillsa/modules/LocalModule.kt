package com.arakene.fillsa.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.arakene.data.util.AlarmManagerHelper
import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        }
    }

    @Provides
    @Singleton
    fun provideAlarmManagerHelper(
        @ApplicationContext context: Context,
        getNotificationMessageUseCase: GetDailyQuoteNoTokenUseCase
    ): AlarmManagerHelper {
        return AlarmManagerHelper(context, getNotificationMessageUseCase)
    }
}