package com.arakene.presentation.util

import com.arakene.domain.usecase.home.GetDailyQuoteNoTokenUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {
    fun getQuoteUseCase(): GetDailyQuoteNoTokenUseCase
    fun getAlarmManager(): AlarmManagerHelper
}