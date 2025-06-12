package com.arakene.fillsa.modules

import android.content.Context
import androidx.room.Room
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.db.QuoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DBModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): QuoteDatabase =
        Room.databaseBuilder(context = context, QuoteDatabase::class.java, "dbName")
            .build()

    @Singleton
    @Provides
    fun provideDao(db: QuoteDatabase): LocalQuoteInfoDao = db.localQuoteDao()

}