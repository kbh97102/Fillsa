package com.arakene.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_quote_info")
data class WidgetQuoteInfoEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val dailyQuoteSeq: Int,

    val likeYn: String,

    val imagePath: String?,

    val korQuote: String?,

    val engQuote: String?,

    val korAuthor: String?,

    val engAuthor: String?,

    val authorUrl: String?,

    val date: String
)
