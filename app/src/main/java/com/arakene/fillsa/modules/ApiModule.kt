package com.arakene.fillsa.modules

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.util.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    fun provideApi(
        tokenInterceptor: TokenInterceptor
    ): FillsaApi {
        return Retrofit.Builder()
            .baseUrl("https://www.fillsa.store/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .addInterceptor(tokenInterceptor)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FillsaApi::class.java)
    }

    @Provides
    fun provideNoTokenApi(): FillsaNoTokenApi {
        return Retrofit.Builder()
            .baseUrl("https://www.fillsa.store/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
            .create(FillsaNoTokenApi::class.java)
    }


}