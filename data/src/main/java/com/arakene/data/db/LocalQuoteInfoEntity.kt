package com.arakene.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quoteInfo")
data class LocalQuoteInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val dailyQuoteSeq: Int,

    val korQuote: String,

    val engQuote: String,

    val korAuthor: String,

    val engAuthor: String,

    val typing: String,

    val likeYn: String,

    val memo: String

)