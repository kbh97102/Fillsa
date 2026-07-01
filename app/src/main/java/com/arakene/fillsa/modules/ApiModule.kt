package com.arakene.fillsa.modules

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.network.TokenApi
import com.arakene.data.util.AuthAuthenticator
import com.arakene.data.util.HttpLogInterceptor
import com.arakene.data.util.TokenInterceptor
import com.arakene.data.util.VersionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    private val baseUrl = "https://api.fillsa.com/"

    @Provides
    @Named("refreshClient")
    fun provideRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(VersionInterceptor())
            .addInterceptor(HttpLogInterceptor())
            .build()
    }

    @Provides
    @Named("refreshRetrofit")
    fun provideRefreshRetrofit(@Named("refreshClient") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideTokenRefreshApi(@Named("refreshRetrofit") retrofit: Retrofit): TokenApi {
        return retrofit.create(TokenApi::class.java)
    }

    @Provides
    fun provideApi(
        tokenInterceptor: TokenInterceptor,
        auth: AuthAuthenticator
    ): FillsaApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(VersionInterceptor())
                    .authenticator(auth)
                    .addInterceptor(tokenInterceptor)
                    .addInterceptor(HttpLogInterceptor())
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FillsaApi::class.java)
    }

    @Provides
    fun provideNoTokenApi(): FillsaNoTokenApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(VersionInterceptor())
                    .addInterceptor(HttpLogInterceptor())
                    .build()
            )
            .build()
            .create(FillsaNoTokenApi::class.java)
    }


}
