package com.arakene.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_info")
data class StreakInfoEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String, // yyyy-MM-dd

    @ColumnInfo(name = "streak_date_count")
    val streakDateCount: Int = 0,

    @ColumnInfo(name = "is_daily_writing_completed")
    val isDailyWritingCompleted: Boolean = false
)
