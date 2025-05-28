package com.arakene.fillsa.modules

import com.arakene.data.network.FillsaApi
import com.arakene.data.repository.LoginRepositoryImpl
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

}