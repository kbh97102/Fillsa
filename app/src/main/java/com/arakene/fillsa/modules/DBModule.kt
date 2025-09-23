package com.arakene.fillsa.modules

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.db.QuoteDatabase
import com.arakene.data.db.WidgetQuoteInfoDao
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
            .addMigrations(MIGRATION_1_2)
            .build()

    @Singleton
    @Provides
    fun provideDao(db: QuoteDatabase): LocalQuoteInfoDao = db.localQuoteDao()

    @Singleton
    @Provides
    fun provideWidgetDao(db: QuoteDatabase): WidgetQuoteInfoDao = db.widgetQuoteDao()


    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 새 Entity에 맞는 테이블 생성
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `widget_quote_info` (
                `id` INTEGER NOT NULL,
                `likeYn` TEXT NOT NULL,
                `imagePath` TEXT,
                `korQuote` TEXT,
                `engQuote` TEXT,
                `korAuthor` TEXT,
                `engAuthor` TEXT,
                `authorUrl` TEXT,
                `date` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
            )
        }
    }
}