package com.arakene.data.db

import androidx.room.Database

@Database(entities = [LocalQuoteInfoEntity::class], version = 1)
abstract class QuoteDatabase {

    abstract fun localQuoteDao(): LocalQuoteInfoDao

}