package com.arakene.fillsa.modules

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.db.QuoteDatabase
import com.arakene.data.db.StreakInfoDao
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
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .build()

    @Singleton
    @Provides
    fun provideDao(db: QuoteDatabase): LocalQuoteInfoDao = db.localQuoteDao()

    @Singleton
    @Provides
    fun provideWidgetDao(db: QuoteDatabase): WidgetQuoteInfoDao = db.widgetQuoteDao()

    @Singleton
    @Provides
    fun provideStreakInfoDao(db: QuoteDatabase): StreakInfoDao = db.streakInfoDao()

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

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1. 임시 테이블 생성
            database.execSQL("""
            CREATE TABLE widget_quote_info_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dailyQuoteSeq INTEGER NOT NULL,
                likeYn TEXT NOT NULL,
                imagePath TEXT,
                korQuote TEXT,
                engQuote TEXT,
                korAuthor TEXT,
                engAuthor TEXT,
                authorUrl TEXT,
                date TEXT NOT NULL
            )
        """)

            // 2. 기존 데이터 복사 (기존 'id' 컬럼을 'dailyQuoteSeq'로 복사)
            database.execSQL("""
            INSERT INTO widget_quote_info_new 
            (dailyQuoteSeq, likeYn, imagePath, korQuote, engQuote, korAuthor, engAuthor, authorUrl, date)
            SELECT id, likeYn, imagePath, korQuote, engQuote, korAuthor, engAuthor, authorUrl, date
            FROM widget_quote_info
        """)

            // 3. 기존 테이블 삭제
            database.execSQL("DROP TABLE widget_quote_info")

            // 4. 새 테이블 이름 변경
            database.execSQL("ALTER TABLE widget_quote_info_new RENAME TO widget_quote_info")

        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS streak_info (
                date TEXT NOT NULL PRIMARY KEY,
                streak_date_count INTEGER NOT NULL DEFAULT 0,
                is_daily_writing_completed INTEGER NOT NULL DEFAULT 0
            )
            """
            )
        }
    }

}