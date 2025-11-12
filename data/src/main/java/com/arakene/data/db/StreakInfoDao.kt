package com.arakene.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StreakInfoDao {

    // ✅ Create / Update (PK 충돌 시 업데이트)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(streakInfo: StreakInfoEntity)

    // ✅ Read (단일 조회)
    @Query("SELECT * FROM streak_info WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): StreakInfoEntity?

    // ✅ Read (전체 조회)
    @Query("SELECT * FROM streak_info ORDER BY date DESC")
    suspend fun getAll(): List<StreakInfoEntity>

    // ✅ Delete (단일 삭제)
    @Query("DELETE FROM streak_info WHERE date = :date")
    suspend fun deleteByDate(date: String)

    // ✅ Delete (전체 삭제)
    @Query("DELETE FROM streak_info")
    suspend fun deleteAll()
}
