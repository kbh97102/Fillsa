package com.arakene.data.repository

import android.content.Context
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.domain.repository.WidgetRepository
import com.arakene.domain.responses.DailyQuotaNoToken
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepositoryImpl @Inject constructor(
    private val api: FillsaNoTokenApi,
) : WidgetRepository {



    override suspend fun getData(date: String): DailyQuotaNoToken? {
        return api.getDailyQuoteNonMember(date).body()
    }
}