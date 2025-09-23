package com.arakene.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetQuoteInfoDao {

    @Insert
    suspend fun insert(data: WidgetQuoteInfoEntity)

    @Query("SELECT * FROM widget_quote_info WHERE date = :date")
    fun get(date: String): Flow<WidgetQuoteInfoEntity>
}