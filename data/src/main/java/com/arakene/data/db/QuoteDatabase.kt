package com.arakene.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalQuoteInfoEntity::class, WidgetQuoteInfoEntity::class, StreakInfoEntity::class],
    version = 4
)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun localQuoteDao(): LocalQuoteInfoDao

    abstract fun widgetQuoteDao(): WidgetQuoteInfoDao

    abstract fun streakInfoDao(): StreakInfoDao

}