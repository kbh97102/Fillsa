package com.arakene.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocalQuoteInfoDao {

    @Query("SELECT * FROM quoteInfo")
    suspend fun getAllQuotes(): List<LocalQuoteInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: LocalQuoteInfoEntity)

    @Delete
    suspend fun deleteQuote(quote: LocalQuoteInfoEntity)

    @Update
    fun updateQuote(quote: LocalQuoteInfoEntity)

    @Query("SELECT * FROM quoteInfo ORDER BY id ASC LIMIT 10 OFFSET :offset")
    fun getPagingList(offset: Int): List<LocalQuoteInfoEntity>

    @Query("UPDATE quoteInfo SET memo = :memo WHERE dailyQuoteSeq = :seq")
    suspend fun updateMemo(memo: String, seq: Int)

    @Query("UPDATE quoteinfo SET likeYn = :likeYn WHERE dailyQuoteSeq = :seq")
    suspend fun updateLike(likeYn: String, seq: Int): Int
}