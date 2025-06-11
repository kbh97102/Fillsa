package com.arakene.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalQuoteInfoEntity::class], version = 1)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun localQuoteDao(): LocalQuoteInfoDao

}