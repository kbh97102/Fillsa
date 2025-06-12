package com.arakene.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "quoteInfo")
data class LocalQuoteInfoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val dailyQuoteSeq: Int,

    val korQuote: String,

    val engQuote: String,

    val korAuthor: String,

    val engAuthor: String,

    val korTyping: String,

    val engTyping: String,

    val likeYn: String,

    val memo: String,

    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),

    val dayOfWeek: String = LocalDate.now().dayOfWeek.name
)