package com.arakene.fillsa.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arakene.data.network.FillsaApi
import com.arakene.data.repository.LocalRepositoryImpl
import com.arakene.data.repository.LoginRepositoryImpl
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
    fun provideLoginRepository(api: FillsaApi): LoginRepository {
        return LoginRepositoryImpl(api)
    }

    @Provides
    fun provideLocalRepository(dataStore: DataStore<Preferences>): LocalRepository {
        return LocalRepositoryImpl(dataStore)
    }
}