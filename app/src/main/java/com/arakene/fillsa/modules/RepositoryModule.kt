package com.arakene.fillsa.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.repository.HomeRepositoryImpl
import com.arakene.data.repository.ListRepositoryImpl
import com.arakene.data.repository.LocalRepositoryImpl
import com.arakene.data.repository.LoginRepositoryImpl
import com.arakene.data.util.TokenProvider
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.repository.LoginRepository
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
    fun provideLocalRepository(
        dataStore: DataStore<Preferences>,
        tokenProvider: TokenProvider
    ): LocalRepository {
        return LocalRepositoryImpl(dataStore, tokenProvider)
    }

    @Provides
    fun provideHomeRepository(api: FillsaApi, noTokenApi: FillsaNoTokenApi): HomeRepository {
        return HomeRepositoryImpl(api = api, nonTokenApi = noTokenApi)
    }

    @Provides
    fun provideListRepository(api: FillsaApi) = ListRepositoryImpl(api)
}