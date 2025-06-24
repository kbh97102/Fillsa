package com.arakene.fillsa.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.network.TokenApi
import com.arakene.data.repository.CalendarRepositoryImpl
import com.arakene.data.repository.CommonRepositoryImpl
import com.arakene.data.repository.HomeRepositoryImpl
import com.arakene.data.repository.ListRepositoryImpl
import com.arakene.data.repository.LocalRepositoryImpl
import com.arakene.data.repository.LoginRepositoryImpl
import com.arakene.data.repository.TokenRepositoryImpl
import com.arakene.data.repository.TypingRepositoryImpl
import com.arakene.data.repository.WidgetRepositoryImpl
import com.arakene.data.util.TokenProvider
import com.arakene.domain.repository.CalendarRepository
import com.arakene.domain.repository.CommonRepository
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.repository.ListRepository
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.repository.LoginRepository
import com.arakene.domain.repository.TokenRepository
import com.arakene.domain.repository.TypingRepository
import com.arakene.domain.repository.WidgetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideLoginRepository(api: FillsaNoTokenApi): LoginRepository {
        return LoginRepositoryImpl(api)
    }

    @Provides
    fun provideWidgetRepository(api: FillsaNoTokenApi): WidgetRepository = WidgetRepositoryImpl(api)

    @Provides
    fun provideLocalRepository(
        dataStore: DataStore<Preferences>,
        tokenProvider: TokenProvider,
        dao: LocalQuoteInfoDao
    ): LocalRepository {
        return LocalRepositoryImpl(dataStore, tokenProvider, dao)
    }

    @Provides
    fun provideHomeRepository(api: FillsaApi, noTokenApi: FillsaNoTokenApi): HomeRepository {
        return HomeRepositoryImpl(api = api, nonTokenApi = noTokenApi)
    }

    @Provides
    fun provideListRepository(api: FillsaApi): ListRepository = ListRepositoryImpl(api)

    @Provides
    fun provideCalendarRepository(
        api: FillsaApi,
        noTokenApi: FillsaNoTokenApi
    ): CalendarRepository = CalendarRepositoryImpl(api, noTokenApi)

    @Provides
    fun provideCommonRepository(api: FillsaNoTokenApi, tokenApi: FillsaApi): CommonRepository =
        CommonRepositoryImpl(api, tokenApi)

    @Provides
    fun provideTokenRepository(tokenApi: TokenApi): TokenRepository = TokenRepositoryImpl(tokenApi)

    @Provides
    fun provideTypingRepository(api: FillsaApi): TypingRepository = TypingRepositoryImpl(api)
}