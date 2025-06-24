package com.arakene.domain.repository

import android.content.Context
import com.arakene.domain.responses.DailyQuotaNoToken
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface WidgetRepository {

    suspend fun getData(date: String): DailyQuotaNoToken?
}