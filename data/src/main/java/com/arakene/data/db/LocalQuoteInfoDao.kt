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

    @Query("SELECT * FROM quoteInfo WHERE id = :seq")
    suspend fun findQuoteById(seq: Int): LocalQuoteInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: LocalQuoteInfoEntity)

    @Delete
    suspend fun deleteQuote(quote: LocalQuoteInfoEntity)

    @Query("DELETE FROM quoteInfo WHERE id = :seq")
    suspend fun deleteQuoteById(seq: Int)

    @Update
    fun updateQuote(quote: LocalQuoteInfoEntity)

    @Query("SELECT * FROM quoteInfo ORDER BY id ASC LIMIT 10 OFFSET :offset")
    fun getPagingList(offset: Int): List<LocalQuoteInfoEntity>

    @Query("SELECT * FROM quoteInfo WHERE likeYn = :likeYn ORDER BY id ASC LIMIT 10 OFFSET :offset")
    fun getPagingListWithLike(offset: Int, likeYn: String): List<LocalQuoteInfoEntity>

    @Query("UPDATE quoteInfo SET memo = :memo WHERE id = :seq")
    suspend fun updateMemo(memo: String, seq: Int)

    @Query("UPDATE quoteinfo SET likeYn = :likeYn WHERE id = :seq")
    suspend fun updateLike(likeYn: String, seq: Int): Int

    @Query("SELECT * FROM quoteInfo WHERE id = :seq")
    suspend fun getQuote(seq: Int): LocalQuoteInfoEntity?

    @Query("DELETE FROM quoteInfo")
    suspend fun clear()
}